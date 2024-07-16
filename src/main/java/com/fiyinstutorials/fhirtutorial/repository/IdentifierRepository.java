package com.fiyinstutorials.fhirtutorial.repository;

import com.fiyinstutorials.fhirtutorial.model.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierRepository extends JpaRepository<Identifier, Long> {

}
