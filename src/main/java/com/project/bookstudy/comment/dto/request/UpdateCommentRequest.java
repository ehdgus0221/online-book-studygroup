package com.project.bookstudy.comment.dto.request;

import com.project.bookstudy.comment.domain.param.UpdateCommentParam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequest {

    private String content;


    @Builder
    private UpdateCommentRequest(String content) {
        this.content = content;
    }

    public UpdateCommentParam getUpdateCommentParam() {
        return UpdateCommentParam.builder()
                .content(content)
                .build();
    }
}
