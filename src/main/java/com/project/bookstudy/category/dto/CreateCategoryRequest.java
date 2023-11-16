package com.project.bookstudy.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateCategoryRequest {

    private Long parentCategoryId;
    private Long studyGroupId;
    private String subject;

    @Builder
    private CreateCategoryRequest(Long parentCategoryId, Long studyGroupId, String subject) {
        this.parentCategoryId = parentCategoryId;
        this.studyGroupId = studyGroupId;
        this.subject = subject;
    }
}
