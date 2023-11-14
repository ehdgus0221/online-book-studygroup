package com.project.bookstudy.enrollment.domain;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "enrollment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}