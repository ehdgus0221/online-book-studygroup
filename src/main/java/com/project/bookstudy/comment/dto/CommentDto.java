package com.project.bookstudy.comment.dto;

import com.project.bookstudy.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {

    private Long id;
    private String content;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;

    private boolean isDeleted;

    @Builder
    public CommentDto(Long id, String content, boolean isDeleted, LocalDateTime regDt, LocalDateTime udtDt) {
        this.id = id;
        this.content = content;
        this.isDeleted = isDeleted;
        this.regDt = regDt;
        this.udtDt = udtDt;
    }

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .regDt(LocalDateTime.now())
                .udtDt(LocalDateTime.now())
                .build();
    }
}
