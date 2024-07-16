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
@Table(name = "payment_notice")
public class PaymentNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "payment_notice_id")
    private Long id;

    private String EHRCategoryTag;

    @Column(name = "payment_notice_server_id")
    private String noticeId;

    private String noticeUniqueId;
    private String status;
    private String requestReference;
    private String responseReference;
    private LocalDateTime created;
    private String reporterReference;
    private String paymentReference;
    private LocalDate paymentDate;
    private String payeeReference;

    @ManyToOne
    @JoinColumn(name = "recipient_reference_id")
    private Identifier recipientReference;

    private String amountCurrency;
    private Double amountValue;
    private String paymentStatus;

    @OneToMany(mappedBy = "paymentNotice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;
}
