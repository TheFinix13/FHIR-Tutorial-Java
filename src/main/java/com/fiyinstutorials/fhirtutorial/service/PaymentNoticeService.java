package com.fiyinstutorials.fhirtutorial.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.gclient.IQuery;
import com.fiyinstutorials.fhirtutorial.model.Coding;
import com.fiyinstutorials.fhirtutorial.model.Identifier;
import com.fiyinstutorials.fhirtutorial.repository.AccountRepository;
import com.fiyinstutorials.fhirtutorial.repository.PaymentNoticeRepository;
import com.fiyinstutorials.fhirtutorial.responseDTO.CodingDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.IdentifierDTO;
import com.fiyinstutorials.fhirtutorial.responseDTO.paymentnotice.PaymentNoticeResponse;
import com.fiyinstutorials.fhirtutorial.utils.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentNoticeService {
    @Value("${hapi.server.base-url}")
    private String hapiServerBaseUrl;

    @Value("${hapi.server.tag}")
    private String hapiServerTag;

    private final FhirContext fhirContext = FhirContext.forR4();
    private final PaymentNoticeRepository paymentNoticeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentNoticeService(AccountRepository accountRepository, PaymentNoticeRepository paymentNoticeRepository, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.paymentNoticeRepository = paymentNoticeRepository;

    }


    public void savePaymentNotices() {
        List<PaymentNoticeResponse> paymentNoticeResponses = getAllPaymentNotices();

        if (paymentNoticeResponses.isEmpty()) {
            log.info("No PaymentNotices to save.");
            return;
        }

        log.info("Saving {} PaymentNotices to the database.", paymentNoticeResponses.size());

        paymentNoticeResponses.forEach(response -> {
            try {
                // Convert PaymentNoticeResponse DTO to the PaymentNotice entity
                com.fiyinstutorials.fhirtutorial.model.PaymentNotice paymentNoticeEntity = new com.fiyinstutorials.fhirtutorial.model.PaymentNotice();
                paymentNoticeEntity.setPaymentNoticeId(response.getPaymentNoticeId());
                paymentNoticeEntity.setPaymentNoticeUniqueId(response.getPaymentNoticeUniqueId());
                paymentNoticeEntity.setStatus(response.getStatus());
                paymentNoticeEntity.setRequestReference(response.getRequestReference());
                paymentNoticeEntity.setResponseReference(response.getResponseReference());
                paymentNoticeEntity.setCreated(response.getCreated());
                paymentNoticeEntity.setReporterReference(response.getReporterReference());
                paymentNoticeEntity.setPaymentReference(response.getPaymentReference());
                paymentNoticeEntity.setPaymentDate(response.getPaymentDate());
                paymentNoticeEntity.setPayeeReference(response.getPayeeReference());
                paymentNoticeEntity.setRecipientIdentifierSystem(response.getRecipientIdentifierSystem());
                paymentNoticeEntity.setRecipientIdentifierValue(response.getRecipientIdentifierValue());
                paymentNoticeEntity.setAmountCurrency(response.getAmountCurrency());
                paymentNoticeEntity.setAmountValue(response.getAmountValue());
                paymentNoticeEntity.setPaymentStatus(mapPaymentStatusList(response.getPaymentStatus(), paymentNoticeEntity));
                paymentNoticeEntity.setIdentifiers(mapIdentifierList(response.getIdentifiers(), paymentNoticeEntity));

                if (paymentNoticeRepository.existsByPaymentNoticeId(paymentNoticeEntity.getPaymentNoticeId())) {
                    log.info("Skipping existing PaymentNotice with ID: {}", paymentNoticeEntity.getPaymentNoticeId());
                } else {
                    paymentNoticeRepository.save(paymentNoticeEntity);
                    log.info("Saved new PaymentNotice with ID: {}", paymentNoticeEntity.getPaymentNoticeId());
                }
            }catch (Exception e) {
                log.error("Error processing PaymentNotice with ID: {}", response.getPaymentNoticeId(), e);
                // Skip this entry and continue with the next
            }
        });

        log.info("Total PaymentNotices saved: {}", paymentNoticeResponses.size());
    }
    private List<Coding> mapPaymentStatusList(List<CodingDTO> codingDTOs, com.fiyinstutorials.fhirtutorial.model.PaymentNotice paymentNoticeEntity) {
        return codingDTOs.stream()
                .map(dto -> {
                    Coding coding = new Coding();
                    coding.setTypeSystem(dto.getSystem());
                    coding.setTypeCode(dto.getCode());
                    coding.setPaymentNotice(paymentNoticeEntity);
                    return coding;
                })
                .collect(Collectors.toList());
    }

    // Method to map List<IdentifierDTO> to List<Identifier>
    private List<Identifier> mapIdentifierList(List<IdentifierDTO> identifierDTOs, com.fiyinstutorials.fhirtutorial.model.PaymentNotice paymentNoticeEntity) {
        return identifierDTOs.stream()
                .map(dto -> {
                    Identifier identifier = new Identifier();
                    identifier.setIdentifierSystem(dto.getSystem());
                    identifier.setIdentifierValue(dto.getValue());
                    identifier.setResourceType(dto.getResourceType());
                    identifier.setResourceId(dto.getResourceId());
                    identifier.setPaymentNotice(paymentNoticeEntity);
                    return identifier;
                })
                .collect(Collectors.toList());
    }


    public List<PaymentNoticeResponse> getAllPaymentNotices() {
        List<PaymentNoticeResponse> paymentNoticeResponses = new ArrayList<>();
        int pageOffset = 0;
        int pageCount = 100; // Adjust batch size as needed

        while (true) {
            Bundle bundle = fetchPaymentNoticesByPage(pageOffset, pageCount);

            if (bundle == null || bundle.getEntry().isEmpty()) {
                log.info("No more PaymentNotices to fetch or bundle is empty.");
                break;
            }

            List<PaymentNoticeResponse> responses = bundle.getEntry().stream()
                    .filter(entry -> entry.getResource() instanceof org.hl7.fhir.r4.model.PaymentNotice) // Filter for PaymentNotice only
                    .map(entry -> {
                        try {
                            org.hl7.fhir.r4.model.PaymentNotice paymentNotice = (org.hl7.fhir.r4.model.PaymentNotice) entry.getResource();
                            return mapToPaymentNoticeDTO(paymentNotice);
                        }catch (Exception e) {
                            log.error("Error mapping PaymentNotice: {}", entry.getFullUrl(), e);
                            return null; // Skip this entry
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (responses.isEmpty()) {
                log.info("No PaymentNotice entries found in the current bundle.");
            } else {
                log.info("Fetched {} PaymentNotice entries from the current bundle.", responses.size());
            }
            paymentNoticeResponses.addAll(responses);
            pageOffset += pageCount;
        }
        log.info("Total PaymentNotices fetched: {}", paymentNoticeResponses.size());
        return paymentNoticeResponses;
    }

    private Bundle fetchPaymentNoticesByPage(int pageOffset, int pageCount) {
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        IClientInterceptor loggingInterceptor = new LoggingInterceptor();
        client.registerInterceptor(loggingInterceptor);

        try {
            IQuery<Bundle> query = client.search()
                    .forResource(org.hl7.fhir.r4.model.PaymentNotice.class)
                    .count(pageCount)
                    .offset(pageOffset)
                    .include(org.hl7.fhir.r4.model.PaymentNotice.INCLUDE_REQUEST)
                    .include(org.hl7.fhir.r4.model.PaymentNotice.INCLUDE_RESPONSE)
                    .returnBundle(Bundle.class);

            Bundle bundle = query.execute();
            log.info("Fetched Bundle: {}", bundle); // Log the raw Bundle
            return bundle;
        } catch (Exception e) {
            log.error("Error fetching PaymentNotices: {}", e.getMessage(), e);
            return null;
        }
    }

    private PaymentNoticeResponse mapToPaymentNoticeDTO(org.hl7.fhir.r4.model.PaymentNotice paymentNotice) {
        PaymentNoticeResponse response = new PaymentNoticeResponse();
        String paymentNoticeId = paymentNotice.getIdElement().getIdPart();
        log.info("Mapping PaymentNotice ID: {}", paymentNoticeId);

        response.setPaymentNoticeId(paymentNoticeId);
        response.setPaymentNoticeUniqueId(hapiServerTag+"/" +StatusCodes.RESOURCE_TYPE_PAYMENT_NOTICE+"-"+paymentNoticeId);

        if (paymentNotice.hasStatus()) {
            response.setStatus(mapPaymentNoticeStatus(paymentNotice.getStatus().toCode()));
        }
        if (paymentNotice.hasRequest()) {
            response.setRequestReference(paymentNotice.getRequest().getReference());
        }
        if (paymentNotice.hasResponse()) {
            response.setResponseReference(paymentNotice.getResponse().getReference());
        }
        if (paymentNotice.hasCreated()) {
            response.setCreated(paymentNotice.getCreated());
        }
        if (paymentNotice.hasPayment()) {
            response.setPaymentReference(paymentNotice.getPayment().getReference());
        }
        if (paymentNotice.hasPaymentDate()) {
            response.setPaymentDate(paymentNotice.getPaymentDate());
        }
        if (paymentNotice.hasPayee()) {
            response.setPayeeReference(paymentNotice.getPayee().getReference());
        }
        if (paymentNotice.hasRecipient() && paymentNotice.getRecipient().hasIdentifier()) {
            response.setRecipientIdentifierSystem(paymentNotice.getRecipient().getIdentifier().getSystem());
            response.setRecipientIdentifierValue(paymentNotice.getRecipient().getIdentifier().getValue());
        }
        if (paymentNotice.hasAmount() && paymentNotice.getAmount().hasValue()) {
            response.setAmountCurrency(paymentNotice.getAmount().getCurrency());
            response.setAmountValue(paymentNotice.getAmount().getValue().doubleValue());
        }
        if (paymentNotice.hasPaymentStatus()) {
            response.setPaymentStatus(paymentNotice.getPaymentStatus().getCoding().stream()
                    .filter(coding -> coding.getCode() != null)
                    .map(coding -> {
                        CodingDTO codingDTO = new CodingDTO();
                        codingDTO.setSystem(coding.getSystem());
                        codingDTO.setCode(mapPaymentStatus(coding.getCode()));
                        return codingDTO;
                    })
                    .collect(Collectors.toList()));
        }
        if (paymentNotice.hasIdentifier()) {
            response.setIdentifiers(paymentNotice.getIdentifier().stream()
                    .map(identifier -> {
                        IdentifierDTO identifierDTO = new IdentifierDTO();
                        identifierDTO.setSystem(identifier.getSystem());
                        identifierDTO.setValue(identifier.getValue());
                        identifierDTO.setResourceType(StatusCodes.RESOURCE_TYPE_PAYMENT_NOTICE);
                        identifierDTO.setResourceId(response.getPaymentNoticeUniqueId());
                        return identifierDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private String mapPaymentNoticeStatus(String statusCode) {
        switch (statusCode) {
            case "active":
                return StatusCodes.PAYMENT_NOTICE_CODE_ACTIVE;
            case "cancelled":
                return StatusCodes.PAYMENT_NOTICE_CODE_CANCELLED;
            case "draft":
                return StatusCodes.PAYMENT_NOTICE_CODE_DRAFT;
            case "entered-in-error":
                return StatusCodes.PAYMENT_NOTICE_CODE_ERROR;
            default:
                return statusCode;
        }
    }
    private String mapPaymentStatus(String statusCode) {

        switch (statusCode) {
            case "paid":
                return StatusCodes.PAYMENT_STATUS_CODE_PAID;
            case "cleared":
                return StatusCodes.PAYMENT_STATUS_CODE_CLEARED;
            default:
                log.warn("Unexpected payment status code: {}", statusCode);
                return "Unknown Status Code: " + statusCode; // Handle unexpected values
        }
    }
}


