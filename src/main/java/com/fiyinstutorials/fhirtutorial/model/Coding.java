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
@Table(name = "coding")
public class Coding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeSystem;
    private String typeCode;

    @ManyToOne
    @JoinColumn(name = "claim_response_id")
    private ClaimResponse claimResponse;

    @ManyToOne
    @JoinColumn(name = "payment_notice_id")
    private PaymentNotice paymentNotice;
}
