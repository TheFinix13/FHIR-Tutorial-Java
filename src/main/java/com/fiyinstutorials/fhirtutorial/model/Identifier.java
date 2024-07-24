package com.fiyinstutorials.fhirtutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "identifier")

public class Identifier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "identifier_id")
    private Long id;

    private String resourceType;
    private Long resourceId;
    private String identifierSystem;
    private String identifierValue;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "payment_notice_id")
    private PaymentNotice paymentNotice;

    @ManyToOne
    @JoinColumn(name = "claim_response_id")
    private ClaimResponse claimResponse;


}


