package com.project.bookstudy.category.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategoryResponse {

    private Boolean isRoot;
    private Long categoryId;
    private List<CategoryDto> childCategories = new ArrayList<>();

    @Builder
    private CategoryResponse(Long categoryId, List<CategoryDto> childCategories) {
        this.isRoot = categoryId == null;
        this.categoryId = categoryId;
        this.childCategories = childCategories;
    }
}
