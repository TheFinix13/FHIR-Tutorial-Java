package com.fiyinstutorials.fhirtutorial.responseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountReferenceDTO {
    private String reference;
    private String display;
}
