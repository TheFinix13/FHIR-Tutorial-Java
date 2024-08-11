package com.fiyinstutorials.fhirtutorial.requestDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CROutcomeUpdateRequest {
    private String claimResponseId;
    private String Outcome;
}
