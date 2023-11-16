package com.project.bookstudy.enrollment.repository;

import com.project.bookstudy.enrollment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
