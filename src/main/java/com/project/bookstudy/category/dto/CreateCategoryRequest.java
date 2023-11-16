package com.project.bookstudy.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class CreateCategoryRequest {

    private Long parentCategoryId;
    private Long studyGroupId;
    private String subject;

}
