package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.enrollment.domain.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomEnrollmentRepository {
    public Optional<Enrollment> findByIdWithAll(Long id);

    public Page<Enrollment> searchEnrollment(Pageable pageable, Long memberId);
}
