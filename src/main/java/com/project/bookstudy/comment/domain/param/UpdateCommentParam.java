package com.project.bookstudy.comment.domain.param;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateCommentParam {
    private Long id;
    private String content;

    @Builder
    public UpdateCommentParam(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}
