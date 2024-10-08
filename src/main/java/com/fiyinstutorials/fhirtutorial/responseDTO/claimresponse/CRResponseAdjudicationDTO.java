package com.fiyinstutorials.fhirtutorial.responseDTO.claimresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CRResponseAdjudicationDTO {
    private String adjudicationCategoryCode;
    private String adjudicationReasonSystem;
    private String adjudicationReasonCode;
    private String adjudicationReasonDisplay;
    private Double adjudicationAmountValue;
    private String adjudicationAmountCurrency;
    private Double adjudicationQuantityValue;
}
