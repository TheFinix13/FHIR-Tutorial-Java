package com.fiyinstutorials.fhirtutorial.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClaimResponse {
    private String EHRCategoryTag;
    private String claimId;
    private String claimUniqueId;
    private String claimStatus;
    private String use;
    private String PatientReference;
    private LocalDateTime billablePeriodStart;
    private LocalDateTime billablePeriodEnd;
    private LocalDateTime created;
    private String EnterReference;
    private String InsurerReference;
    private String ProviderReference;
    private String priority;
    private String payeeType;
    private String partyReference;
    private String treatmentReference;
    private String currency;
    private Double value;
    private int itemSequence;
    private LocalDate serviceDate;
    private LocalDateTime servicePeriodStart;
    private LocalDateTime servicePeriodEnd;
    private String patientPaidCurrency;
    private Double patientPaidValue;
    private String unitPriceCurrency;
    private Double unitPriceValue;
    private String TotalTaxCurrency;
    private Double TotalTaxValue;
    private String NetCostCurrency;
    private Double NetCostValue;
    private String totalClaimCostCurrency;
    private Double totalClaimCostValue;
    private List<IdentifierDTO> identifiers = new ArrayList<>();

}
