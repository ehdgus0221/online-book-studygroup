package com.project.bookstudy.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCommentResponse {

    private Long commentId;

    @Builder
    private CreateCommentResponse(Long commentId) {
        this.commentId = commentId;
    }
}
