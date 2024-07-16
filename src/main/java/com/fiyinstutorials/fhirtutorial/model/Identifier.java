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
@Table (name = "identifier")
public class Identifier {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonIgnore
        @Column(name = "identifier_id")
        private Long id;

        private String resourceType;
        private Long resourceId;
        private String system;
        private String value;

        @ManyToOne
        @JoinColumn(name = "patient_id")
        private Patient patient;

        @ManyToOne
        @JoinColumn(name = "account_id")
        private Account account;

        @ManyToOne
        @JoinColumn(name = "charge_item_id")
        private ChargeItem chargeItem;

        @ManyToOne
        @JoinColumn(name = "claim_id")
        private Claim claim;

        @ManyToOne
        @JoinColumn(name = "invoice_id")
        private Invoice invoice;

        @ManyToOne
        @JoinColumn(name = "payment_notice_id")
        private PaymentNotice paymentNotice;

}
