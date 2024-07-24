package com.fiyinstutorials.fhirtutorial.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "claim_response_item")
public class ClaimResponseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_response_item_id")
    private Long id;

    private int itemSequence;

    @OneToMany(mappedBy = "claimResponseItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClaimResponseAdjudication> adjudication;

    @ManyToOne
    @JoinColumn(name = "claim_response_id")
    private ClaimResponse claimResponse;
}
