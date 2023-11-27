package com.project.bookstudy.study_group.dto.response;

import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class CreateStudyGroupResponse {

    private Long studyGroupId;
    private Long leaderId;

    @Builder
    private CreateStudyGroupResponse(Long studyGroupId, Long leaderId) {
        this.studyGroupId = studyGroupId;
        this.leaderId = leaderId;
    }

    public static CreateStudyGroupResponse fromStudyGroup(StudyGroup studyGroup) {
        return CreateStudyGroupResponse.builder()
                .studyGroupId(studyGroup.getId())
                .leaderId(studyGroup.getLeader().getId())
                .build();

    }
}
