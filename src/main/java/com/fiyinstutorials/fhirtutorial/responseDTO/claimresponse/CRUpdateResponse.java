package com.fiyinstutorials.fhirtutorial.responseDTO.claimresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CRUpdateResponse {

    private String Outcome;
//    private List<CodingDTO> paymentType = new ArrayList<>();

}
