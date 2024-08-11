package com.fiyinstutorials.fhirtutorial.repository;

import com.fiyinstutorials.fhirtutorial.model.ClaimResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimResponseRepository extends JpaRepository<ClaimResponse, Long> {

    boolean existsByClaimResponseId(String claimResponseId);

    ClaimResponse findByClaimResponseId(String claimResponseId);
}
