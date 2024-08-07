package com.fiyinstutorials.fhirtutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_server_id")
    private String accountId;

    private String accountUniqueId;
    private String accountName;
    private String accountStatus;
    private LocalDate servicePeriodStart;
    private LocalDate servicePeriodEnd;
    private String ownerReference;
    private String description;
    private String aggregate;
    private String term;
    private Boolean estimate;
    private String currency;
    private Double value;
    private String calculateAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<AccountReference> subject;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountCoverage> accountCoverages;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Identifier> identifiers;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

}
