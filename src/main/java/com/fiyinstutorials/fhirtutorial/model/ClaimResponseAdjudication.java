package com.fiyinstutorials.fhirtutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "claim_response_adjudication")
public class ClaimResponseAdjudication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_response_adjudication_id")
    private Long id;

    private String adjudicationCategoryCode;
    private String adjudicationReasonSystem;
    private String adjudicationReasonCode;
    private String adjudicationReasonDisplay;
    private Double adjudicationAmountValue;
    private String adjudicationAmountCurrency;
    private Double adjudicationQuantityValue;

    @ManyToOne
    @JoinColumn(name = "claim_response_item_id")
    private ClaimResponseItem claimResponseItem;

    @ManyToOne
    @JoinColumn(name = "claim_response__id")
    private ClaimResponse claimResponse;
}
