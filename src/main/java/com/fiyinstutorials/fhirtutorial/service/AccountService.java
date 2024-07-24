package com.fiyinstutorials.fhirtutorial.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fiyinstutorials.fhirtutorial.model.AccountCoverage;
import com.fiyinstutorials.fhirtutorial.model.AccountReference;
import com.fiyinstutorials.fhirtutorial.model.Identifier;
import com.fiyinstutorials.fhirtutorial.model.Patient;
import com.fiyinstutorials.fhirtutorial.repository.*;
import com.fiyinstutorials.fhirtutorial.responseDTO.AccountResponse;
import com.fiyinstutorials.fhirtutorial.responseDTO.AccountCoverageDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.IdentifierDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.AccountReferenceDTO;
import com.fiyinstutorials.fhirtutorial.utils.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Account;
import org.hl7.fhir.r4.model.Bundle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountService {
    @Value("${hapi.server.base-url}")
    private String hapiServerBaseUrl;

    @Value("${hapi.server.tag}")
    private String hapiServerTag;

    private final FhirContext fhirContext = FhirContext.forR4();
    private final AccountRepository accountRepository;
    private final PatientRepository patientRepository;
    private final IdentifierRepository identifierRepository;
    private final AccountCoverageRepository accountCoverageRepository;
    private final AccountReferenceRepository accountReferenceRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountService(IdentifierRepository identifierRepository, ModelMapper modelMapper, RestTemplate restTemplate, AccountRepository accountRepository, PatientRepository patientRepository, AccountCoverageRepository accountCoverageRepository, AccountReferenceRepository accountReferenceRepository) {
        this.accountRepository = accountRepository;
        this.identifierRepository = identifierRepository;
        this.modelMapper = modelMapper;
        this.patientRepository = patientRepository;
        this.accountCoverageRepository = accountCoverageRepository;
        this.accountReferenceRepository = accountReferenceRepository;
    }

    @Transactional
    public List<AccountResponse> fetchAndSaveAccounts() {
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        List<AccountResponse> allAccountResponses = new ArrayList<>();

        List<Patient> allPatients = patientRepository.findAll();
        List<com.fiyinstutorials.fhirtutorial.model.Account> allAccounts = new ArrayList<>();

        // Process each patient and their corresponding accounts
        for (Patient patient : allPatients) {
            List<AccountResponse> accountResponses = getAccountsForPatient(client, patient.getPatientId());
            for (AccountResponse accountResponse : accountResponses) {
                com.fiyinstutorials.fhirtutorial.model.Account account = modelMapper.map(accountResponse, com.fiyinstutorials.fhirtutorial.model.Account.class);
                boolean patientFound = false;

                // Check subject references
                if (accountResponse.getSubject() != null && !accountResponse.getSubject().isEmpty()) {
                    for (AccountReferenceDTO subject : accountResponse.getSubject()) {
                        String subjectReference = subject.getReference();
                        if (subjectReference != null && subjectReference.startsWith("Patient/")) {
                            String subjectPatientId = subjectReference.split("/")[1];
                            if (patient.getPatientId().equals(subjectPatientId)) {
                                account.setPatient(patient);
                                patientFound = true;
                                break;
                            }
                        }
                    }
                }

                // Check identifier matches if subject not found
                if (!patientFound && accountResponse.getIdentifiers() != null && !accountResponse.getIdentifiers().isEmpty()) {
                    for (IdentifierDTO identifierDTO : accountResponse.getIdentifiers()) {
                        for (Identifier patientIdentifier : patient.getIdentifiers()) {
                            if (identifierDTO.getValue().equals(patientIdentifier.getIdentifierValue())) {
                                account.setPatient(patient);
                                patientFound = true;
                                break;
                            }
                        }
                    }
                }

                // Check name matches if subject and identifier not found
                if (!patientFound && accountResponse.getAccountName() != null) {
                    String[] nameParts = accountResponse.getAccountName().split(" ");
                    if (nameParts.length > 1) {
                        String firstName = nameParts[0];
                        String lastName = nameParts[nameParts.length - 1];
                        if (patient.getFirstName().equalsIgnoreCase(firstName) && patient.getLastName().equalsIgnoreCase(lastName)) {
                            account.setPatient(patient);
                            patientFound = true;
                        }
                    }
                }

                allAccounts.add(account);
                allAccountResponses.add(accountResponse);
            }
        }

        // Save all accounts and their related entities
        for (com.fiyinstutorials.fhirtutorial.model.Account account : allAccounts) {
            com.fiyinstutorials.fhirtutorial.model.Account savedAccount = accountRepository.save(account);
            log.info("Saved account with ID: {}", savedAccount.getAccountId());

            // Save related entities
            AccountResponse accountResponse = allAccountResponses.stream()
                    .filter(ar -> ar.getAccountId().equals(savedAccount.getAccountId()))
                    .findFirst()
                    .orElse(null);

            if (accountResponse != null) {
                saveIdentifiers(accountResponse, savedAccount);
                saveCoverages(accountResponse, savedAccount);
                saveReferences(accountResponse, savedAccount);
            }
        }
        return allAccountResponses;
    }

    private List<AccountResponse> getAccountsForPatient(IGenericClient client, String patientId) {
        List<AccountResponse> accountResponses = new ArrayList<>();

        Bundle accountBundle = client.search()
                .forResource(Account.class)
                .where(Account.PATIENT.hasId(patientId))
                .where(Account.SUBJECT.hasAnyOfIds(patientId))
                .returnBundle(Bundle.class)
                .execute();

        if (accountBundle != null) {
            accountBundle.getEntry().forEach(entry -> {
                Account accountResource = (Account) entry.getResource();
                AccountResponse accountResponse = new AccountResponse();

                accountResponse.setAccountId(accountResource.getIdElement().getIdPart());
                accountResponse.setEHRCategoryTag(hapiServerTag);
                accountResponse.setAccountUniqueId(hapiServerTag + "-" + accountResource.getIdElement().getIdPart());

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
                    accountResponse.setOwnerReference(accountResource.getOwner().getReference());
                }
                if (accountResource.hasDescription()) {
                    accountResponse.setDescription(accountResource.getDescription());
                }
                if (accountResource.hasSubject()) {
                    List<AccountReferenceDTO> subjects = accountResource.getSubject().stream()
                            .map(subject -> AccountReferenceDTO.builder()
                                    .reference(subject.getReference())
                                    .display(subject.getDisplay())
                                    .build())
                            .collect(Collectors.toList());
                    accountResponse.setSubject(subjects);
                }
                if (accountResource.hasCoverage()) {
                    List<AccountCoverageDTO> coverages = accountResource.getCoverage().stream()
                            .map(coverage -> AccountCoverageDTO.builder()
                                    .coverage(AccountReferenceDTO.builder()
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

                log.info("Added account with ID: {}", accountResponse.getAccountId());

            });
        }

        return accountResponses;
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

    private void saveIdentifiers(AccountResponse accountResponse, com.fiyinstutorials.fhirtutorial.model.Account account) {
        if (accountResponse.getIdentifiers() != null) {
            List<Identifier> identifiers = accountResponse.getIdentifiers().stream()
                    .map(dto -> {
                        Identifier identifier = new Identifier();
                        identifier.setResourceType(StatusCodes.RESOURCE_TYPE_ACCOUNT);
                        identifier.setResourceId(account.getId());
                        identifier.setIdentifierSystem(dto.getSystem());
                        identifier.setIdentifierValue(dto.getValue());
                        return identifier;
                    })
                    .collect(Collectors.toList());
            identifierRepository.saveAll(identifiers);
        }
    }

    private void saveCoverages(AccountResponse accountResponse, com.fiyinstutorials.fhirtutorial.model.Account account) {
        if (accountResponse.getCoverages() != null) {
            List<AccountCoverage> accountCoverages = accountResponse.getCoverages().stream()
                    .map(dto -> {
                        AccountCoverage accountCoverage = new AccountCoverage();
                        accountCoverage.setCoverage(modelMapper.map(dto.getCoverage(), AccountReference.class));
                        accountCoverage.setPriority(dto.getPriority());
                        accountCoverage.setAccount(account);
                        return accountCoverage;
                    })
                    .collect(Collectors.toList());
            accountCoverageRepository.saveAll(accountCoverages);
        }
    }

    private void saveReferences(AccountResponse accountResponse, com.fiyinstutorials.fhirtutorial.model.Account account) {
        if (accountResponse.getSubject() != null) {
            List<AccountReference> accountReferences = accountResponse.getSubject().stream()
                    .map(dto -> {
                        AccountReference accountReference = new AccountReference();
                        accountReference.setReference(dto.getReference());
                        accountReference.setDisplay(dto.getDisplay());
                        accountReference.setAccount(account);
                        return accountReference;
                    })
                    .collect(Collectors.toList());
            accountReferenceRepository.saveAll(accountReferences);
        }
    }


}

