package com.project.bookstudy.enrollment.repository;

import com.project.bookstudy.enrollment.domain.Enrollment;
import com.project.bookstudy.study_group.repository.CustomEnrollmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, CustomEnrollmentRepository {
}
