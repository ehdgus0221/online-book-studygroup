package com.project.bookstudy.comment.domain.param;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateCommentParam {
    private String content;

    @Builder
    public UpdateCommentParam(String content) {
        this.content = content;
    }
}
