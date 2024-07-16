package com.fiyinstutorials.fhirtutorial.repository;

import com.fiyinstutorials.fhirtutorial.model.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
}
