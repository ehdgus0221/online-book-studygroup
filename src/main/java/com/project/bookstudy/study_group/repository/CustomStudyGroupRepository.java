package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.request.StudyGroupSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStudyGroupRepository {
    Page<StudyGroup> searchStudyGroup(Pageable pageable, StudyGroupSearchCond cond);
}
