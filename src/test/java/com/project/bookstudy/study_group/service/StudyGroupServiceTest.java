package com.project.bookstudy.study_group.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.response.CreateStudyGroupResponse;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class StudyGroupServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private StudyGroupService studyGroupService;
    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @DisplayName("스터디 그룹 생성 성공")
    @Test
    @Transactional
    void createStudyGroupSuccess() {
        //given

        Member member1 = createMember("donghyeon", "dlaehdgus23@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();
        Member member2 = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(member2.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");

        //when
        StudyGroupDto response = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());


        //then
        StudyGroup studyGroup = studyGroupRepository.findById(response.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        assertThat(studyGroup.getId()).isNotNull();
        assertThat(studyGroup.getSubject()).isEqualTo(request.getSubject());
        assertThat(studyGroup.getContents()).isEqualTo(request.getContents());
        assertThat(studyGroup.getContentsDetail()).isEqualTo(request.getContentsDetail());
        assertThat(studyGroup.getStudyStartDt()).isEqualTo(request.getStudyStartDt());
        assertThat(studyGroup.getStudyEndDt()).isEqualTo(request.getStudyEndDt());
        assertThat(studyGroup.getMaxSize()).isEqualTo(request.getMaxSize());
        assertThat(studyGroup.getPrice()).isEqualTo(request.getPrice());
        assertThat(studyGroup.getRecruitmentStartDt()).isEqualTo(request.getRecruitmentStartDt());
        assertThat(studyGroup.getRecruitmentEndDt()).isEqualTo(request.getRecruitmentEndDt());
        assertThat(studyGroup.getLeader().getId()).isEqualTo(request.getMemberId());

    }

    @DisplayName("스터디 그룹 생성 실패 - authentication을 통해 사용자 정보 찾지 못하는 경우")
    @Test
    @Transactional
    void createStudyGroupFailed_1() {
        //given

        Member member1 = createMember("donghyeon", "dlaehdgus@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();
        String memberEmail = authentication.getName();

        //when
        //then
        assertThat(member1.getEmail()).isNotEqualTo(memberEmail);
    }

    @DisplayName("스터디 그룹 생성 성공")
    @Test
    @Transactional
    void createStudyGroupFailed_2() {
        //given

        Member member1 = createMember("donghyeon", "dlaehdgus23@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();
        Member member2 = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(member2.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject", "contents");

        //when
        StudyGroupDto response = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());


        //then
        StudyGroup studyGroup = studyGroupRepository.findById(response.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        assertThat(studyGroup.getId()).isNotNull();
        assertThat(studyGroup.getSubject()).isNotEqualTo("subject1");
        assertThat(studyGroup.getContents()).isNotEqualTo("contents1");
        assertThat(studyGroup.getContentsDetail()).isNotEqualTo("contentsDetail1");
        assertThat(studyGroup.getStudyStartDt()).isNotEqualTo(LocalDateTime.of(2023, 11, 1, 0, 0, 0));
        assertThat(studyGroup.getStudyEndDt()).isNotEqualTo(LocalDateTime.of(2023, 11, 1, 0, 0, 0));
        assertThat(studyGroup.getMaxSize()).isNotEqualTo(8);
        assertThat(studyGroup.getPrice()).isNotEqualTo(10000L);
        assertThat(studyGroup.getRecruitmentStartDt()).isNotEqualTo(LocalDateTime.of(2021, 10, 1, 0, 0, 0));
        assertThat(studyGroup.getRecruitmentEndDt()).isNotEqualTo(LocalDateTime.of(2021, 10, 1, 0, 0, 0));
        assertThat(studyGroup.getLeader().getId()).isEqualTo(request.getMemberId());

    }

    private Member createMember(String name, String email) {
        return Member.builder()
                .name(name)
                .career("career")
                .phone("phone")
                .email(email)
                .build();
    }

    private CreateStudyGroupRequest createStudyCreateGroupRequest(Long memberId, LocalDateTime studyStartDt, LocalDateTime studyEndDt, LocalDateTime recruitmentStartDt, LocalDateTime recruitmentEndDt, String subject, String contents) {
        return CreateStudyGroupRequest.builder()
                .memberId(memberId)
                .subject(subject)
                .contents(contents)
                .contentsDetail("contentsDetail")
                .maxSize(10)
                .price(25000L)
                .studyStartDt(studyStartDt)
                .studyEndDt(studyEndDt)
                .recruitmentStartDt(recruitmentStartDt)
                .recruitmentEndDt(recruitmentEndDt)
                .build();
    }

    private Authentication createAuthentication() {

        String email = "dlaehdgus23@naver.com";
        String password = "ehdgus1234";

        return new UsernamePasswordAuthenticationToken(email, password,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

}