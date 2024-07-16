package com.fiyinstutorials.fhirtutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "charge_item")
public class ChargeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "charge_item_id")
    private Long id;

    private String EHRCategoryTag;

    @Column(name = "charge_item_server_id")
    private String chargeItemId;

    private String chargeItemUniqueId;
    private String status;
    private String subjectReference;
    private String encounterReference;
    private LocalDateTime occurrenceDateTime;
    private LocalDateTime occurrencePeriodStart;
    private LocalDateTime occurrencePeriodEnd;

    @ManyToOne
    @JoinColumn(name = "performing_organization_id")
    private Identifier performingOrganization;

    @ManyToOne
    @JoinColumn(name = "requesting_organization_id")
    private Identifier requestingOrganization;

    private Double quantityValue;
    private String totalPriceComponentType;
    private String totalPriceComponentFactor;
    private String totalPriceComponentCurrency;
    private Double totalPriceComponentValue;
    private String overrideReason;
    private LocalDateTime enteredDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "charge_item_id")
    private List<Reference> reasonReferences;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "charge_item_id")
    private List<Reference> serviceReferences;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_reference_id")
    private Reference productReferences;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_reference_id")
    private Reference accountReferences;

    @OneToMany(mappedBy = "chargeItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;

}
