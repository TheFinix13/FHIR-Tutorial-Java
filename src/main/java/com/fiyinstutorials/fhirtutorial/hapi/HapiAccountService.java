package com.fiyinstutorials.fhirtutorial.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fiyinstutorials.fhirtutorial.repository.AccountRepository;
import com.fiyinstutorials.fhirtutorial.repository.IdentifierRepository;
import com.fiyinstutorials.fhirtutorial.responseDTO.AccountResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.CoverageDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.IdentifierDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.ReferenceDTO;
import com.fiyinstutorials.fhirtutorial.utils.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Account;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Reference;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HapiAccountService {

    @Value("${hapi.server.base-url}")
    private String hapiServerBaseUrl;

    @Value("${hapi.server.tag}")
    private String hapiServerTag;

    private final FhirContext fhirContext = FhirContext.forR4();
    private final AccountRepository accountRepository;
    private final IdentifierRepository identifierRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;

    @Autowired
    public HapiAccountService(IdentifierRepository identifierRepository, ModelMapper modelMapper, RestTemplate restTemplate, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.identifierRepository = identifierRepository;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
    }

    public List<AccountResponse> getPatientAccounts() {
        List<AccountResponse> accountResponses = new ArrayList<>();

        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        int offset = 0;
        int count = 100;
        int length = 0;

        do {
            Bundle query = fetchAccountRecordFromApi(client, offset, count);

            if (query != null) {
                List<Bundle.BundleEntryComponent> bundleEntry = query.getEntry();
                length = bundleEntry.size();

                bundleEntry
                        .parallelStream()
                        .forEach(entry -> processAccountRecord(entry, accountResponses));

                offset++;
            }

        } while (length != 0);

        return accountResponses;
    }

    private Bundle fetchAccountRecordFromApi(IGenericClient client, int offset, int count) {

        return client.search()
                .forResource(Account.class)
                .returnBundle(Bundle.class)
                .offset(offset)
                .count(count)
                .execute();
    }

    private void processAccountRecord(Bundle.BundleEntryComponent entry, List<AccountResponse> accountResponses) {

        Account accountResource = (Account) entry.getResource();
        String accountId = accountResource.getIdElement().getIdPart();

        if (accountRepository.existsByAccountId(accountId)) {
            log.info("Account with ID {} already exists in the database, skipping save.", accountId);
            return;
        }

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountId(accountId);
        accountResponse.setEHRCategoryTag(hapiServerTag);
        accountResponse.setAccountUniqueId(hapiServerTag + "-" + accountId);

        if (accountResource.hasName()) {
            accountResponse.setAccountName(accountResource.getName());
        }
        if (accountResource.hasStatus()) {
            accountResponse.setAccountStatus(mapAccountStatus(accountResource.getStatus().toCode()));
        }
        if (accountResource.hasServicePeriod()) {
            if (accountResource.getServicePeriod().hasStart()) {
                accountResponse.setServicePeriodStart(accountResource.getServicePeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if (accountResource.getServicePeriod().hasEnd()) {
                accountResponse.setServicePeriodEnd(accountResource.getServicePeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        }
        if (accountResource.hasOwner()) {
            Reference owner = accountResource.getOwner();
            ReferenceDTO ownerReferenceDTO = ReferenceDTO.builder()
                    .reference(owner.getReference())
                    .display(owner.getDisplay())
                    .build();
            accountResponse.setOwnerReference(ownerReferenceDTO);
        }
        if (accountResource.hasDescription()) {
            accountResponse.setDescription(accountResource.getDescription());
        }
        if (accountResource.hasSubject()) {
            List<ReferenceDTO> subjects = accountResource.getSubject().stream()
                    .map(subject -> ReferenceDTO.builder()
                            .reference(subject.getReference())
                            .display(subject.getDisplay())
                            .build())
                    .collect(Collectors.toList());
            accountResponse.setSubject(subjects);
        }
        if (accountResource.hasCoverage()) {
            List<CoverageDTO> coverages = accountResource.getCoverage().stream()
                    .map(coverage -> CoverageDTO.builder()
                            .coverage(ReferenceDTO.builder()
                                    .reference(coverage.getCoverage().getReference())
                                    .display(coverage.getCoverage().getDisplay())
                                    .build())
                            .priority(coverage.hasPriority() ? coverage.getPriority() : 0)
                            .build())
                    .collect(Collectors.toList());
            accountResponse.setCoverages(coverages);
        }
        if (accountResource.hasIdentifier()) {
            List<IdentifierDTO> identifiers = accountResource.getIdentifier().stream()
                    .map(identifier -> IdentifierDTO.builder()
                            .system(identifier.getSystem())
                            .value(identifier.getValue())
                            .build())
                    .collect(Collectors.toList());
            accountResponse.setIdentifiers(identifiers);
        }

        accountResponses.add(accountResponse);
        saveAccountResponse(accountResponse);
        log.info("Added account with ID: {}", accountResponse.getAccountId());

    }

    private String mapAccountStatus(String fhirStatus) {
        switch (fhirStatus) {
            case "active":
                return StatusCodes.ACCOUNT_STATUS_ACTIVE;
            case "inactive":
                return StatusCodes.ACCOUNT_STATUS_INACTIVE;
            case "entered-in-error":
                return StatusCodes.ACCOUNT_STATUS_ERROR;
            case "on-hold":
                return StatusCodes.ACCOUNT_STATUS_ON_HOLD;
            case "unknown":
                return StatusCodes.ACCOUNT_STATUS_UNKNOWN;
            default:
                return fhirStatus;
        }
    }

    private void saveAccountResponse(AccountResponse accountResponse) {
        // Convert AccountResponse to Account entity if necessary
        com.fiyinstutorials.fhirtutorial.model.Account accountEntity = modelMapper.map(accountResponse, com.fiyinstutorials.fhirtutorial.model.Account.class);

        // Map identifiers and associate with accountEntity
        if (accountResponse.getIdentifiers() != null) {
            List<com.fiyinstutorials.fhirtutorial.model.Identifier> identifiers = accountResponse.getIdentifiers().stream()
                    .map(dto -> {
                        com.fiyinstutorials.fhirtutorial.model.Identifier identifier = new com.fiyinstutorials.fhirtutorial.model.Identifier();
                        identifier.setResourceType(StatusCodes.RESOURCE_TYPE_ACCOUNT);
                        identifier.setResourceId(accountEntity.getId()); // Assuming pkId is the resource id in your system
                        identifier.setSystem(dto.getSystem());
                        identifier.setValue(dto.getValue());
                        return identifier;
                    })
                    .collect(Collectors.toList());

            identifierRepository.saveAll(identifiers);
        }

        accountRepository.save(accountEntity);
        log.info("Saved account with ID: {}", accountEntity.getAccountId());
    }


}
