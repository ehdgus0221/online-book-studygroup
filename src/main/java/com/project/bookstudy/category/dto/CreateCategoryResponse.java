package com.project.bookstudy.category.dto;

import com.project.bookstudy.category.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCategoryResponse {

    private Long categoryId;
    private Long studyGroupId;
    private String subject;

    @Builder
    private CreateCategoryResponse(Long categoryId, Long studyGroupId, String subject) {
        this.categoryId = categoryId;
        this.studyGroupId = studyGroupId;
        this.subject = subject;
    }

    public static CreateCategoryResponse fromCategory(Category category) {
        return CreateCategoryResponse.builder()
                .categoryId(category.getId())
                .studyGroupId(category.getStudyGroup().getId())
                .subject(category.getSubject())
                .build();
    }

}
