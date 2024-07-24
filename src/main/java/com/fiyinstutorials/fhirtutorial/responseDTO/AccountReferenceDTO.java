package com.fiyinstutorials.fhirtutorial.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountReferenceDTO {
    private String reference;
    private String display;
}