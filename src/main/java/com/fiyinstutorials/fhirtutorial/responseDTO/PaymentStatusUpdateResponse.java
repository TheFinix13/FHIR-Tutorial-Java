package com.fiyinstutorials.fhirtutorial.responseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentStatusUpdateResponse {
    //Patient
    private String patientId;
    private String patientUniqueId;
    private String firstName;
    private String lastName;

    //Claim Response
    private String claimResponseId;
    private String claimResponseUniqueId;
    private String claimResponseStatus;
    private String claimOutcome;
    private String claimDisposition;
    private List<CodingDTO> claimResponsePaymentType = new ArrayList<>();


    //Payment Notice
    private String paymentNoticeId;
    private String paymentNoticeUniqueId;
    private String paymentNoticeStatus;
    private List<CodingDTO> paymentStatus = new ArrayList<>();

}
