package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.enrollment.domain.Enrollment;
import com.project.bookstudy.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.project.bookstudy.enrollment.domain.QEnrollment.enrollment;
import static com.project.bookstudy.enrollment.domain.QPayment.payment;
import static com.project.bookstudy.member.domain.QMember.member;
import static com.project.bookstudy.study_group.domain.QStudyGroup.studyGroup;

@RequiredArgsConstructor
public class CustomEnrollmentRepositoryImpl implements CustomEnrollmentRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Enrollment> findByIdWithAll(Long id) {

        QMember leader = new QMember("m");

        Enrollment result = jpaQueryFactory.selectFrom(enrollment)
                .join(enrollment.studyGroup, studyGroup).fetchJoin()
                .join(studyGroup.leader, leader).fetchJoin()
                .join(enrollment.payment, payment).fetchJoin()
                .join(enrollment.member, member).fetchJoin()
                .where(enrollment.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
