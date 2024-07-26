package com.fiyinstutorials.fhirtutorial.responseDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentNoticeResponse {
    private String paymentNoticeId;
    private String paymentNoticeUniqueId;
    private String status;
    private String requestReference;
    private String responseReference;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date created;

    private String reporterReference;
    private String paymentReference;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date paymentDate;

    private String payeeReference;
    private String recipientIdentifierSystem;
    private String recipientIdentifierValue;
    private String amountCurrency;
    private Double amountValue;
    private List<CodingDTO> paymentStatus = new ArrayList<>();
    private List<IdentifierDTO> identifiers = new ArrayList<>();

}
