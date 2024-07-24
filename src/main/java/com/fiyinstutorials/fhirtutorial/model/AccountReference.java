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
@Table(name = "reference")
public class AccountReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id")
    private Long id;

    private String reference;
    private String display;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
