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
public class CRResponse {
    private String claimResponseId;
    private String claimResponseUniqueId;
    private String status;
    private String use;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date created;

    private String patientReference;
    private String insurerIdentifierSystem;
    private String insurerIdentifierValue;
    private String RequestorReference;
    private String RequestReference;
    private String Outcome;
    private String disposition;
    private List<CodingDTO> payeeType = new ArrayList<>();

    private List<CRResponseItemDTO> item = new ArrayList<>();
    private List<CRResponseAdjudicationDTO> total = new ArrayList<>();
    private List<IdentifierDTO> identifier = new ArrayList<>();

    //Payment
    private List<CodingDTO> paymentType = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date paymentDate;

    private String paymentAdjustmentCurrency;
    private Double paymentAdjustmentValue;
    private String paymentAdjustmentReason;
    private String paymentAmountCurrency;
    private Double paymentAmountValue;
    private String paymentIdentifierSystem;
    private String paymentIdentifierValue;


}
