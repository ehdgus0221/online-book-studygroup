package com.project.bookstudy.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.enrollment.domain.Enrollment;
import com.project.bookstudy.enrollment.repository.EnrollmentRepository;
import com.project.bookstudy.enrollment.repository.PaymentRepository;
import com.project.bookstudy.enrollment.service.EnrollmentService;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.EnrollmentDto;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.response.CreateStudyGroupResponse;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication1, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        //when
        Long enrollmentId = enrollmentService.enroll(studyGroup.getId(), authentication2);
        //then
        Enrollment findEnrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        assertThat(findEnrollment.getId()).isEqualTo(enrollmentId);

    }

    @DisplayName("스터디 그룹 신청 실패 - 스터디 그룹장(리더)는 신청 불가")
    @Test
    void enrollFailed1() {

        //given
        Long memberPoint = 50_000L;

        Member leader = createMember("스터디 리더", "leader@naver.com");
        Member member = createMember("스터디 멤버", "member@naver.com");

        member.chargePoint(memberPoint);
        Authentication authentication1 = createAuthenticationLeader();

        memberRepository.saveAll(List.of(leader, member));

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication1, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        //when
        //then
        assertThatThrownBy(() -> enrollmentService.enroll(studyGroup.getId(), authentication1))
                .isInstanceOf(MemberException.class)
                .hasMessageContaining(ErrorCode.LEADER_ENROLLMENT_ERROR.getDescription());
    }

    @Test
    @DisplayName("스터디 그룹 단일조회 성공")
    @Transactional
    void getEnrollmentSuccess() {
        //given
        Member member1 = createMember("member","member@naver.com");
        memberRepository.save(member1);

        Member leader1 = createMember("leader", "leader@naver.com");
        memberRepository.save(leader1);

        Authentication authentication1 = createAuthenticationLeader();
        Authentication authentication2 = createAuthenticationMember();

        Long originMemberPoint = 1000000L;
        member1.chargePoint(originMemberPoint);

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication1, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));


        Long enrollmentId = enrollmentService.enroll(studyGroup.getId(), authentication2);
        //then
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();

        //when
        EnrollmentDto enrollmentDto = enrollmentService.getEnrollment(enrollment.getId());

        //then
        assertThat(enrollmentDto.getId()).isEqualTo(enrollment.getId());
        assertThat(enrollmentDto.getStatus()).isEqualTo(enrollment.getStatus());
        assertThat(enrollmentDto.getPayment().getPrice()).isEqualTo(enrollment.getPayment().getPrice());
        assertThat(enrollmentDto.getPayment().getStatus()).isEqualTo(enrollment.getPayment().getStatus());
        assertThat(enrollmentDto.getStudyGroup().getId()).isEqualTo(enrollment.getStudyGroup().getId());
    }

    @Test
    @DisplayName("스터디 그룹 단일조회 실패 - 신청 id 존재하지 않는 경우")
    @Transactional
    void getEnrollmentFailed() {
        //given
        Member member1 = createMember("member","member@naver.com");
        memberRepository.save(member1);

        Member leader1 = createMember("leader", "leader@naver.com");
        memberRepository.save(leader1);

        Authentication authentication1 = createAuthenticationLeader();
        Authentication authentication2 = createAuthenticationMember();

        Long originMemberPoint = 1000000L;
        member1.chargePoint(originMemberPoint);

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication1, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));


        Long enrollmentId = enrollmentService.enroll(studyGroup.getId(), authentication2);

        //when
        //then
        assertThat(enrollmentRepository.findById(enrollmentId + 1)).isEmpty();

    }

    @Test
    @DisplayName("스터디 그룹 신청내역 전체조회 성공")
    @Transactional
    void getEnrollmentListSuccess() {
        //given
        Member member1 = createMember("member","member@naver.com");
        memberRepository.save(member1);

        Member leader1 = createMember("leader", "leader@naver.com");
        memberRepository.save(leader1);

        Authentication authentication1 = createAuthenticationLeader();
        Authentication authentication2 = createAuthenticationMember();

        Long originMemberPoint = 1000000L;
        member1.chargePoint(originMemberPoint);

        CreateStudyGroupRequest request1 = createStudyCreateGroupRequest(leader1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication1, request1.toStudyGroupParam());

        CreateStudyGroupRequest request2 = createStudyCreateGroupRequest(leader1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response2 = studyGroupService.createStudyGroup(authentication1, request1.toStudyGroupParam());

        StudyGroup studyGroup1 = studyGroupRepository.findById(response1.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        StudyGroup studyGroup2 = studyGroupRepository.findById(response2.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));


        Long enrollmentId1 = enrollmentService.enroll(studyGroup1.getId(), authentication2);
        Long enrollmentId2 = enrollmentService.enroll(studyGroup2.getId(), authentication2);

        Optional<Enrollment> enrollment1 = enrollmentRepository.findById(enrollmentId1);
        Optional<Enrollment> enrollment2 = enrollmentRepository.findById(enrollmentId2);

        List<Optional<Enrollment>> enrollmentList = new ArrayList<>();
        enrollmentList.add(enrollment1);
        enrollmentList.add(enrollment2);


        //then
        assertThat(enrollmentList.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("스터디 그룹 신청내역 전체조회 실패 - 신청내역 개수가 일치하지 않는 경우")
    @Transactional
    void getEnrollmentListFailed() {
        //given
        Member member1 = createMember("member","member@naver.com");
        memberRepository.save(member1);

        Member leader1 = createMember("leader", "leader@naver.com");
        memberRepository.save(leader1);

        Authentication authentication1 = createAuthenticationLeader();
        Authentication authentication2 = createAuthenticationMember();

        Long originMemberPoint = 1000000L;
        member1.chargePoint(originMemberPoint);

        CreateStudyGroupRequest request1 = createStudyCreateGroupRequest(leader1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication1, request1.toStudyGroupParam());

        CreateStudyGroupRequest request2 = createStudyCreateGroupRequest(leader1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        CreateStudyGroupResponse response2 = studyGroupService.createStudyGroup(authentication1, request1.toStudyGroupParam());

        StudyGroup studyGroup1 = studyGroupRepository.findById(response1.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        StudyGroup studyGroup2 = studyGroupRepository.findById(response2.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));


        Long enrollmentId1 = enrollmentService.enroll(studyGroup1.getId(), authentication2);
        Long enrollmentId2 = enrollmentService.enroll(studyGroup2.getId(), authentication2);

        Optional<Enrollment> enrollment1 = enrollmentRepository.findById(enrollmentId1);
        Optional<Enrollment> enrollment2 = enrollmentRepository.findById(enrollmentId2);

        List<Optional<Enrollment>> enrollmentList = new ArrayList<>();
        enrollmentList.add(enrollment1);
        enrollmentList.add(enrollment2);


        //then
        assertThat(enrollmentList.size()).isNotEqualTo(1);

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
     * @param contents
     * 회원가입 request 값
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
