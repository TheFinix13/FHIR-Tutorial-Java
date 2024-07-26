package com.fiyinstutorials.fhirtutorial.responseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CRResponseItemDTO {
    private int itemSequence;
    private List<CRResponseAdjudicationDTO> adjudication;
}
