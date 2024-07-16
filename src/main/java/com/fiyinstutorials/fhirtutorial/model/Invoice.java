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
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "invoice_id")
    private Long id;

    private String EHRCategoryTag;

    @Column(name = "invoice_server_id")
    private String invoiceId;

    private String invoiceUniqueId;
    private String invoiceStatus;
    private String cancelledReason;
    private String type;
    private String subjectReference;
    private String recipientReference;
    private LocalDateTime creation;
    private LocalDate periodDate;
    private LocalDateTime periodPeriodStart;
    private LocalDateTime periodPeriodEnd;

    @ManyToOne
    @JoinColumn(name = "issuer_reference_id")
    private Identifier issuerReference;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_reference_id")
    private Reference accountReference;

    private int lineItemSequence;
    private String lineItemChargeItemReference;
    private String lineItemPriceComponentType;
    private String lineItemPriceComponentFactor;
    private String lineItemPriceComponentCurrency;
    private Double lineItemPriceComponentValue;
    private String totalNetCurrency;
    private Double totalNetValue;
    private String totalGrossCurrency;
    private Double totalGrossValue;
    private String paymentTerms;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;
}
