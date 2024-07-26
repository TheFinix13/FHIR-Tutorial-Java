package com.fiyinstutorials.fhirtutorial.repository;

import com.fiyinstutorials.fhirtutorial.model.PaymentNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentNoticeRepository extends JpaRepository<PaymentNotice, Long> {
    boolean existsByPaymentNoticeId(String paymentNoticeId);
}
