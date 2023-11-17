package com.project.bookstudy.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateCategoryRequest {
    private Long categoryId;
    private Long parentCategoryId;
    private Boolean toRootCategory;
    private String subject;

    @Builder
    private UpdateCategoryRequest(Long categoryId, Long parentCategoryId, String subject) {
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
        this.subject = subject;
    }
}
