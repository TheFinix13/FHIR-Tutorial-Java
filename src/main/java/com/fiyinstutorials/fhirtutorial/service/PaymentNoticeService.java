package com.fiyinstutorials.fhirtutorial.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fiyinstutorials.fhirtutorial.model.PaymentNotice;
import com.fiyinstutorials.fhirtutorial.repository.AccountRepository;
import com.fiyinstutorials.fhirtutorial.repository.PaymentNoticeRepository;
import com.fiyinstutorials.fhirtutorial.responseDTO.PaymentNoticeResponse;
import com.fiyinstutorials.fhirtutorial.utils.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaymentNoticeService {
    @Value("${hapi.server.base-url}")
    private String hapiServerBaseUrl;

    @Value("${hapi.server.tag}")
    private String hapiServerTag;

    private final FhirContext fhirContext = FhirContext.forR4();
    private final AccountRepository accountRepository;
    private final PaymentNoticeRepository paymentNoticeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentNoticeService(AccountRepository accountRepository, PaymentNoticeRepository paymentNoticeRepository, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.paymentNoticeRepository = paymentNoticeRepository;
    }

    public List<PaymentNoticeResponse> fetchAndSavePaymentNotices() {
        IGenericClient client = fhirContext.newRestfulGenericClient(hapiServerBaseUrl);
        List<PaymentNoticeResponse> allPaymentNoticeResponses = new ArrayList<>();

        accountRepository.findAll().forEach(account -> {
            List<PaymentNoticeResponse> paymentNoticeResponses = getPaymentNoticesForAccount(client, account.getAccountId());
            paymentNoticeResponses.forEach(paymentNoticeResponse -> {
                PaymentNotice paymentNotice = modelMapper.map(paymentNoticeResponse, PaymentNotice.class);
                paymentNoticeRepository.save(paymentNotice);
                log.info("Saved payment notice for account ID: {}", account.getAccountId());
            });
            allPaymentNoticeResponses.addAll(paymentNoticeResponses);
        });
        return allPaymentNoticeResponses;
    }

    private List<PaymentNoticeResponse> getPaymentNoticesForAccount(IGenericClient client, String accountId) {
        List<PaymentNoticeResponse> paymentNoticeResponses = new ArrayList<>();

        Bundle paymentNoticeBundle = client.search()
                .forResource(org.hl7.fhir.r4.model.PaymentNotice.class)
                .returnBundle(Bundle.class)
                .execute();

        if (paymentNoticeBundle != null) {
            paymentNoticeBundle.getEntry().forEach(entry -> {
                org.hl7.fhir.r4.model.PaymentNotice paymentNoticeResource = (org.hl7.fhir.r4.model.PaymentNotice) entry.getResource();
                PaymentNoticeResponse paymentNoticeResponse = new PaymentNoticeResponse();

                String noticeId = paymentNoticeResource.getIdElement().getIdPart();
                paymentNoticeResponse.setNoticeId(noticeId);
                paymentNoticeResponse.setEHRCategoryTag(hapiServerTag);
                paymentNoticeResponse.setNoticeUniqueId(hapiServerTag  + "-" + noticeId);

                if (paymentNoticeResource.hasStatus()) {
                    paymentNoticeResponse.setStatus(paymentNoticeResource.getStatus().toCode());
                }
                if (paymentNoticeResource.hasRequest()) {
                    paymentNoticeResponse.setRequestReference(paymentNoticeResource.getRequest().getReference());
                }
                if (paymentNoticeResource.hasResponse()) {
                    paymentNoticeResponse.setResponseReference(paymentNoticeResource.getResponse().getReference());
                }
                if (paymentNoticeResource.hasCreated()) {
                    paymentNoticeResponse.setCreated(paymentNoticeResource.getCreated());
                }
                if (paymentNoticeResource.hasProvider()) {
                    paymentNoticeResponse.setReporterReference(paymentNoticeResource.getProvider().getReference());
                }
                if (paymentNoticeResource.hasPayment()) {
                    paymentNoticeResponse.setPaymentReference(paymentNoticeResource.getPayment().getReference());
                }
                if (paymentNoticeResource.hasPaymentDate()) {
                    paymentNoticeResponse.setPaymentDate(paymentNoticeResource.getPaymentDate());
                }
                if (paymentNoticeResource.hasPayee()) {
                    paymentNoticeResponse.setPayeeReference(paymentNoticeResource.getPayee().getReference());
                }
                if (paymentNoticeResource.hasRecipient()) {
                    paymentNoticeResponse.setRecipientIdentifierSystem(paymentNoticeResource.getRecipient().getIdentifier().getSystem());
                    paymentNoticeResponse.setRecipientIdentifierValue(paymentNoticeResource.getRecipient().getIdentifier().getValue());
                }
                if (paymentNoticeResource.hasAmount()) {
                    paymentNoticeResponse.setAmountValue(paymentNoticeResource.getAmount().getValue().doubleValue());
                    paymentNoticeResponse.setAmountCurrency(paymentNoticeResource.getAmount().getCurrency());
                }
                if (paymentNoticeResource.hasPaymentStatus()) {
                    paymentNoticeResponse.setPaymentStatus(mapPaymentNoticeStatus(paymentNoticeResource.getPaymentStatus().getCodingFirstRep().getCode()));
                }

                paymentNoticeResponses.add(paymentNoticeResponse);

                savePaymentNoticeResponse(paymentNoticeResponse);
                log.info("Added payment notice with ID: {}", paymentNoticeResponse.getNoticeId());
            });
        }

        return paymentNoticeResponses;
    }

    private void savePaymentNoticeResponse(PaymentNoticeResponse paymentNoticeResponse) {
        PaymentNotice paymentNotice = modelMapper.map(paymentNoticeResponse, PaymentNotice.class);
        paymentNoticeRepository.save(paymentNotice);
        log.info("Saved payment notice with ID: {}", paymentNotice.getNoticeId());
    }

    private String mapPaymentNoticeStatus(String statusCode) {
        switch (statusCode) {
            case "paid":
                return StatusCodes.PAYMENT_NOTICE_CODE_ACTIVE;
            case "cleared":
                return StatusCodes.PAYMENT_STATUS_CODE_CLEARED;
            default:
                return statusCode;
        }
    }


}
