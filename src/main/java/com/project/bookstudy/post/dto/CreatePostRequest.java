package com.project.bookstudy.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatePostRequest {
    private String subject;
    private String contents;
    private Long categoryId;
    private Long studyGroupId;
}
