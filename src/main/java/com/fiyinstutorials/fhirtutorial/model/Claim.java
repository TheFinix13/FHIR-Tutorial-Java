package com.fiyinstutorials.fhirtutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "claim")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "claim_id")
    private Long id;

    private String EHRCategoryTag;

    @Column(name = "claim_server_id")
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

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;
}
