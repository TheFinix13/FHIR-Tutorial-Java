package com.fiyinstutorials.fhirtutorial.responseDTO;

import com.fiyinstutorials.fhirtutorial.model.Identifier;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDateTime created;
    private String reporterReference;
    private String paymentReference;
    private LocalDate paymentDate;
    private String payeeReference;
    private Identifier recipientReference;
    private String amountCurrency;
    private Double amountValue;
    private String paymentStatus;
    private List<IdentifierDTO> identifiers = new ArrayList<>();

}
