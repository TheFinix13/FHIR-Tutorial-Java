package com.fiyinstutorials.fhirtutorial.responseDTO;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AccountResponse {
        private String EHRCategoryTag;
        private String accountId;
        private String accountUniqueId;
        private String accountName;
        private String accountStatus;
        private LocalDate servicePeriodStart;
        private LocalDate servicePeriodEnd;
        private ReferenceDTO ownerReference;
        private String description;
        private String aggregate;
        private String term;
        private Boolean estimate;
        private String currency;
        private Double value;
        private String calculateAt;
        private List<ReferenceDTO> subject = new ArrayList<>();
        private List<CoverageDTO> coverages = new ArrayList<>();
        private List<IdentifierDTO> identifiers = new ArrayList<>();

}
