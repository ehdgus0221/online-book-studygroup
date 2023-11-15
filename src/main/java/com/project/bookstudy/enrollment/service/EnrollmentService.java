package com.project.bookstudy.enrollment.service;

import com.project.bookstudy.common.aop.DistributedLock;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.enrollment.domain.Enrollment;
import com.project.bookstudy.enrollment.repository.EnrollmentRepository;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @DistributedLock(key = "#studyGroupId")
    public Long enroll(Long studyGroupId, Authentication authentication) {
        //Collection Fetch Join → Batch Size 적용 고려
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.STUDY_GROUP_NOT_FOUND.getDescription()));

        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        // 본인이 만든 스터디그룹인 경우 신청 불가
        if (studyGroup.getLeader().getId() == member.getId()) {
            throw new MemberException(ErrorCode.LEADER_ENROLLMENT_ERROR);
        }

        // 모집 기간 조회
        if (!studyGroup.isRecruitmentStarted()) {
            throw new IllegalStateException(ErrorCode.RECRUITMENT_DATE_END.getDescription());
        }

        // 중복 신청 검사
        validate(member, studyGroup);

        // 현재 신청 인원 체크
        if (!studyGroup.isApplicable()) {
            throw new IllegalStateException(ErrorCode.STUDY_GROUP_FULL.getDescription());
        }

        Enrollment enrollment = Enrollment.createEnrollment(member, studyGroup);
        enrollmentRepository.save(enrollment);

        return enrollment.getId();
    }

    // 중복 검사
    public void validate(Member member, StudyGroup studyGroup) {
        if (studyGroup.getLeader().getId() == member.getId()) {
            throw new IllegalStateException(ErrorCode.LEADER_ENROLLMENT_ERROR.getDescription());
        }

        validateDuplicateApplication(member, studyGroup);
    }

    private void validateDuplicateApplication(Member member, StudyGroup studyGroup) {
        for (Enrollment enrollment : studyGroup.getEnrollments()) {
            if (enrollment.getMember() == member) {
                throw new IllegalStateException(ErrorCode.DUPLICATE_ENROLLMENT_ERROR.getDescription());
            }
        }
    }
}
