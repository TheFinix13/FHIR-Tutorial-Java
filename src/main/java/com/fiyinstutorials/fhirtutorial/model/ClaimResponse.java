package com.fiyinstutorials.fhirtutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "claim_response")
public class ClaimResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "claim_response_id")
    private Long id;

    private String EHRCategoryTag;

    @Column(name = "claim_response_server_id")
    private String claimResponseId;

    private String claimResponseUniqueId;
    private String claimResponseStatus;
    private String claimResponseUse;
    private String patientReference;
    private Date created;
    private String insurerIdentifierSystem;
    private String insurerIdentifierValue;
    private String RequestorReference;
    private String RequestReference;
    private String Outcome;
    private String disposition;

    @OneToMany(mappedBy = "claimResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coding> payeeType;

    @OneToMany(mappedBy = "claimResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClaimResponseItem> item;

    @OneToMany(mappedBy = "claimResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClaimResponseAdjudication> total;

    //Payment
    @OneToMany(mappedBy = "claimResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coding> paymentType;
    private Date paymentDate;
    private String paymentAdjustmentCurrency;
    private Double paymentAdjustmentValue;
    private String paymentAdjustmentReason;
    private String paymentAmountCurrency;
    private Double paymentAmountValue;
    private String paymentIdentifierSystem;
    private String paymentIdentifierValue;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToMany(mappedBy = "claimResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifier;

}
