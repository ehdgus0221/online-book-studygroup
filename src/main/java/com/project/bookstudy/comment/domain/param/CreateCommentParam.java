package com.project.bookstudy.comment.domain.param;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCommentParam {
    private String content;

    @Builder
    public CreateCommentParam(String content) {
        this.content = content;
    }
}
