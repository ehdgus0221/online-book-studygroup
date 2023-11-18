package com.project.bookstudy.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePostResponse {
    private Long postId;
    private String subject;
    private String contents;
}
