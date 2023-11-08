package com.project.bookstudy.study_group.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyGroupSearchCond {
    private String leaderName;
    private String subject;
}
