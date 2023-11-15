package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.enrollment.domain.Enrollment;

import java.util.Optional;

public interface CustomEnrollmentRepository {
    public Optional<Enrollment> findByIdWithAll(Long id);
}
