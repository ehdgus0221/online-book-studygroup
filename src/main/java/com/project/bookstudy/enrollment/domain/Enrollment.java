package com.project.bookstudy.enrollment.domain;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Builder(access = AccessLevel.PRIVATE)
    public Enrollment(Member member, StudyGroup studyGroup, Payment payment) {

        this.enrollmentStatus = EnrollmentStatus.RESERVED;
        this.member = member;
        this.studyGroup = studyGroup;
        this.payment = payment;
    }

    public static Enrollment createEnrollment(Member member, StudyGroup studyGroup) {
        // Study Group 인원 체크해야 함
        // 동시성 문제도 고려해야 함
        Payment payment = Payment.createPayment(studyGroup, member);

        Enrollment enrollment = Enrollment.builder()
                .member(member)
                .studyGroup(studyGroup)
                .payment(payment)
                .build();

        //양방향 연관관계 설정
        studyGroup.getEnrollments().add(enrollment);

        return enrollment;

    }
}