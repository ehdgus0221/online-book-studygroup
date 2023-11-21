package com.project.bookstudy.post.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePostRequest {
    private Long postId;
    private String subject;
    private String contents;
    private Long categoryId;
}
