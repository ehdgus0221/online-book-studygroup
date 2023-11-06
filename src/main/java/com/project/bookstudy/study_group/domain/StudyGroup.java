package com.project.bookstudy.study_group.domain;

import com.project.bookstudy.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"enrollments"})
public class StudyGroup {

    @Id
    @GeneratedValue
    @Column(name = "study_group_id")
    private Long id;
    private String subject;
    private String contents;
    private String contentsDetail;
    private LocalDateTime studyStartAt;
    private LocalDateTime studyEndAt;
    private Integer maxSize;
    private Long price;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentEndAt;
    private StudyGroupStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader;


    @Builder
    private StudyGroup(String subject, String contents, String contentsDetail, LocalDateTime studyStartAt, LocalDateTime studyEndAt, int maxSize, Long price, LocalDateTime recruitmentStartAt, LocalDateTime recruitmentEndAt, Member leader) {
        this.subject = subject;
        this.contents = contents;
        this.contentsDetail = contentsDetail;
        this.maxSize = maxSize;
        this.price = price;
        this.leader = leader;
        this.studyEndAt = studyEndAt;
        this.studyStartAt = studyStartAt;
        this.recruitmentStartAt = recruitmentStartAt;
        this.recruitmentEndAt = recruitmentEndAt;

        this.status = StudyGroupStatus.RECRUITING;
    }

}
