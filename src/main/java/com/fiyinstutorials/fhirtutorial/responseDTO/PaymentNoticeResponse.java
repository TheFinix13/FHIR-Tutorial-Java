package com.fiyinstutorials.fhirtutorial.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class PaymentNoticeResponse {
    private String EHRCategoryTag;
    private String noticeId;
    private String noticeUniqueId;
    private String status;
    private String requestReference;
    private String responseReference;
    private Date created;
    private String reporterReference;
    private String paymentReference;
    private Date paymentDate;
    private String payeeReference;
    private String recipientIdentifierSystem;
    private String recipientIdentifierValue;
    private String amountCurrency;
    private Double amountValue;
    private String paymentStatus;
    private List<IdentifierDTO> identifiers = new ArrayList<>();

}
