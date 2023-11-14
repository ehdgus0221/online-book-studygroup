package com.project.bookstudy.enrollment.repository;

import com.project.bookstudy.enrollment.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
