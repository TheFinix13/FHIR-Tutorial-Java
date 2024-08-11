package com.fiyinstutorials.fhirtutorial.responseDTO.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiyinstutorials.fhirtutorial.responseDTO.IdentifierDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
        private String accountId;
        private String accountUniqueId;
        private String accountName;
        private String accountStatus;
        private LocalDate servicePeriodStart;
        private LocalDate servicePeriodEnd;
        private String ownerReference;
        private String description;
        private String aggregate;
        private String term;
        private Boolean estimate;
        private String currency;
        private Double value;
        private String calculateAt;
        private List<AccountReferenceDTO> subject = new ArrayList<>();
        private List<AccountCoverageDTO> coverages = new ArrayList<>();
        private List<IdentifierDTO> identifiers = new ArrayList<>();

}
