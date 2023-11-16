package com.project.bookstudy.study_group.repository;

import com.project.bookstudy.enrollment.domain.Enrollment;
import com.project.bookstudy.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    @Override
    public Page<Enrollment> searchEnrollment(Pageable pageable, Long memberId) {

        QMember leader = new QMember("m");

        if (memberId != null) {
            List<Enrollment> enrollmentList = jpaQueryFactory.selectFrom(enrollment)
                    .join(enrollment.studyGroup, studyGroup).fetchJoin()
                    .join(studyGroup.leader, leader).fetchJoin()
                    .join(enrollment.payment, payment).fetchJoin()
                    .join(enrollment.member, member).fetchJoin()
                    .where(member.id.eq(memberId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            Long total = jpaQueryFactory.select(enrollment.count())
                    .from(enrollment)
                    .where(enrollment.member.id.eq(memberId))
                    .fetchOne();

            return new PageImpl<>(enrollmentList, pageable, total);
        } else {
            List<Enrollment> enrollmentList = jpaQueryFactory.selectFrom(enrollment)
                    .join(enrollment.studyGroup, studyGroup).fetchJoin()
                    .join(studyGroup.leader, leader).fetchJoin()
                    .join(enrollment.payment, payment).fetchJoin()
                    .join(enrollment.member, member).fetchJoin()
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            Long total = jpaQueryFactory.select(enrollment.count())
                    .from(enrollment)
                    .fetchOne();

            return new PageImpl<>(enrollmentList, pageable, total);
        }


    }
}
