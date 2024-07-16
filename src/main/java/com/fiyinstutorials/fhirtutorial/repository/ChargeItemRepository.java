package com.fiyinstutorials.fhirtutorial.repository;

import com.fiyinstutorials.fhirtutorial.model.ChargeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeItemRepository extends JpaRepository<ChargeItem, Long> {

}