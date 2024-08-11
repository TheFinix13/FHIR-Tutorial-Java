package com.fiyinstutorials.fhirtutorial.responseDTO.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCoverageDTO {
    private AccountReferenceDTO coverage;
    private int priority;
}
