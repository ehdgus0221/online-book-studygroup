package com.project.bookstudy.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateCategoryRequest {
    private Long parentCategoryId;
    private String subject;

    @Builder
    private UpdateCategoryRequest(Long parentCategoryId, String subject) {
        this.parentCategoryId = parentCategoryId;
        this.subject = subject;
    }
}
