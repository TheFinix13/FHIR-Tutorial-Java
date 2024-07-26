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
@Table(name = "payment_notice")
public class PaymentNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "payment_notice_id")
    private Long id;

    @Column(name = "payment_notice_server_id")
    private String paymentNoticeId;

    private String paymentNoticeUniqueId;
    private String status;
    private String requestReference;
    private String responseReference;
    private Date created;
    private String reporterReference;
    private String paymentReference;
    private Date paymentDate;
    private String payeeReference;
    private String recipientIdentifierSystem;
    private String recipientIdentifierValue;
    private String amountCurrency;
    private Double amountValue;

    @OneToMany(mappedBy = "paymentNotice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coding> paymentStatus;

    @OneToMany(mappedBy = "paymentNotice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;
}
