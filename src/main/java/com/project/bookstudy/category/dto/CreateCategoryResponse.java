package com.project.bookstudy.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCategoryResponse {

    private Long categoryId;

    @Builder
    private CreateCategoryResponse(Long categoryId) {
        this.categoryId = categoryId;
    }

}
