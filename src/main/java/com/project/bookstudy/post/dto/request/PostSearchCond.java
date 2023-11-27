package com.project.bookstudy.post.dto.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class PostSearchCond {

    // 스터디그룹별로 게시글이 나눠져있으므로 studyGroupId는 필수적으로 있어야한다.
    @NotNull
    private Long studyGroupId;
    private Long categoryId;
    private String subject;
    private String contents;

    @Builder
    private PostSearchCond(Long studyGroupId, Long categoryId, String subject, String contents) {
        this.studyGroupId = studyGroupId;
        this.categoryId = categoryId;
        this.subject = subject;
        this.contents = contents;
    }
}
