package com.project.bookstudy.enrollment.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.enrollment.domain.Enrollment;
import com.project.bookstudy.enrollment.domain.EnrollmentStatus;
import com.project.bookstudy.enrollment.repository.EnrollmentRepository;
import com.project.bookstudy.enrollment.repository.PaymentRepository;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Rollback
public class EnrollmentServiceTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyGroupRepository studyGroupRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    StudyGroupService studyGroupService;
    @Autowired
    PaymentRepository paymentRepository;


    @AfterEach
    void tearDown() {
        enrollmentRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        studyGroupRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("스터디 그룹 신청 성공")
    @Test
    void enrollSuccess() {

        //given
        Long memberPoint = 50_000L;

        Member leader = createMember("스터디 리더", "leader@naver.com");
        Member member = createMember("스터디 멤버", "member@naver.com");

        member.chargePoint(memberPoint);
        Authentication authentication1 = createAuthenticationLeader();
        Authentication authentication2 = createAuthenticationMember();

        memberRepository.saveAll(List.of(leader, member));

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        StudyGroupDto response = studyGroupService.createStudyGroup(authentication1, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        //when
        Long enrollmentId = enrollmentService.enroll(studyGroup.getId(), authentication2);
        //then
        Enrollment findEnrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        assertThat(findEnrollment.getId()).isEqualTo(enrollmentId);

    }

    /**
     * @param name
     * @param email 회원가입 메서드
     */
    private Member createMember(String name, String email) {
        return Member.builder()
                .name(name)
                .career("career")
                .phone("phone")
                .email(email)
                .build();
    }

    /**
     * @param memberId
     * @param studyStartDt
     * @param studyEndDt
     * @param recruitmentStartDt
     * @param recruitmentEndDt
     * @param subject
     * @param contents           회원가입 request 값
     */
    private CreateStudyGroupRequest createStudyCreateGroupRequest(Long memberId, LocalDateTime studyStartDt, LocalDateTime studyEndDt, LocalDateTime recruitmentStartDt, LocalDateTime recruitmentEndDt, String subject, String contents) {
        return CreateStudyGroupRequest.builder()
                .memberId(memberId)
                .subject(subject)
                .contents(contents)
                .contentsDetail("contentsDetail")
                .maxSize(10)
                .price(10000L)
                .studyStartDt(studyStartDt)
                .studyEndDt(studyEndDt)
                .recruitmentStartDt(recruitmentStartDt)
                .recruitmentEndDt(recruitmentEndDt)
                .build();
    }

    /**
     * 토큰 인증을 위한 인증 기능 메서드
     */
    private Authentication createAuthenticationLeader() {

        String email = "leader@naver.com";
        String password = "leader123";

        return new UsernamePasswordAuthenticationToken(email, password,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

    private Authentication createAuthenticationMember() {

        String email = "member@naver.com";
        String password = "member123";

        return new UsernamePasswordAuthenticationToken(email, password,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
}
