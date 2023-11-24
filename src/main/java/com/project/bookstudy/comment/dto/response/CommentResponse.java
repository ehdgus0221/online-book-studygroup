package com.project.bookstudy.comment.dto.response;

import com.project.bookstudy.comment.dto.CommentDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponse {

    private Boolean isRoot;
    private Long commentId;
    private List<CommentDto> childComments = new ArrayList<>();

    @Builder
    private CommentResponse(Long commentId, List<CommentDto> childComments) {
        this.isRoot = (commentId == null);
        this.commentId = commentId;
        this.childComments = childComments;

    }
}
