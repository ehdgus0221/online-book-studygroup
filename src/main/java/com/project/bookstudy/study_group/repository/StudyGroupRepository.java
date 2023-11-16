package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, CustomStudyGroupRepository {

    @Query("select s from StudyGroup s left join fetch s.enrollments where s.id = :id")
    Optional<StudyGroup> findByIdFetchEnrollment(@Param("id") Long id);
}
