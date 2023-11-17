package com.project.bookstudy.category.dto;

import com.project.bookstudy.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CategoryResponse {

    private Long categoryId;
    private List<CategoryDto> childCategories = new ArrayList<>();

    @Builder
    private CategoryResponse(Long categoryId, List<CategoryDto> childCategories, Category findParent) {
        this.categoryId = categoryId;
        this.childCategories = childCategories;
    }


}
