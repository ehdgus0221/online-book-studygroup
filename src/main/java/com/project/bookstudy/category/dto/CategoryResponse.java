package com.project.bookstudy.category.dto;

import com.project.bookstudy.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<CategoryResponse> fromEntity(List<CategoryDto> categoryDtoList) {
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        for (CategoryDto categoryDto : categoryDtoList) {
            CategoryResponse categoryResponse = CategoryResponse.builder()
                    .categoryId(categoryDto.getId())
                    .childCategories(new ArrayList<>())
                    .build();
            categoryResponseList.add(categoryResponse);
        }
        return categoryResponseList;
    }

}
