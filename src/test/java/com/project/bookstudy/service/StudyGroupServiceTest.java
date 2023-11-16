package com.project.bookstudy.service;

import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.domain.StudyGroupStatus;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.request.UpdateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @DisplayName("스터디 그룹 생성 실패 - CreateStudyGroupRequest를 통해 만든 스터디그룹 정보와 다른 데이터를 넣어서 Not 비교")
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

    @DisplayName("스터디 그룹 단건 조회 성공")
    @Test
    @Transactional
    void getStudyGroupSuccess() {
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
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject3", "contents3");


        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member2);
        studyGroupRepository.save(studyGroup);


        // then
        //when
        assertThat(studyGroup.getStudyStartDt()).isEqualTo(LocalDateTime.of(2023, 10, 1, 0, 0, 0));
        assertThat(studyGroup.getStudyEndDt()).isEqualTo(LocalDateTime.of(2023, 10, 2, 0, 0, 0));
        assertThat(studyGroup.getRecruitmentStartDt()).isEqualTo(LocalDateTime.of(2023, 9, 1, 0, 0, 0));
        assertThat(studyGroup.getRecruitmentEndDt()).isEqualTo(LocalDateTime.of(2023, 9, 30, 0, 0, 0));
        assertThat(studyGroup.getSubject()).isEqualTo("subject3");
        assertThat(studyGroup.getContents()).isEqualTo("contents3");

    }

    @DisplayName("스터디 그룹 단건 조회 실패 - 해당 스터디 그룹이 DB에 없는 경우")
    @Test
    @Transactional
    void getStudyGroupFailCauseId() {
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
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject3", "contents3");

        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member2);
        studyGroupRepository.save(studyGroup);

        //when //then
        assertThrows(IllegalArgumentException.class, () -> {
            studyGroupService.getStudyGroup(studyGroup.getId() + 1);
        });
    }

    @DisplayName("스터디 그룹 전체 조회 성공")
    @Test
    @Transactional
    void getAllStudyGroupSuccess() {
        //given
        List<StudyGroup> studyGroupList = new ArrayList<>();
        Member member1 = createMember("donghyeon", "dlaehdgus23@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();
        Member member2 = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(member2.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject3", "contents3");

        StudyGroup studyGroup1 = request.toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup2 = request.toCreateServiceParam().toEntityWithLeader(member2);

        //when
        studyGroupList.add(studyGroup1);
        studyGroupList.add(studyGroup2);

        //then
        assertEquals(studyGroupList.size(), 2);
    }

    @DisplayName("스터디 그룹 전체 조회 실패 - 전체 스터디 그룹 수가 일치하지 않는 경우")
    @Test
    @Transactional
    void getAllStudyGroupFailed() {
        //given
        List<StudyGroup> studyGroupList = new ArrayList<>();
        Member member1 = createMember("donghyeon", "dlaehdgus23@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();
        Member member2 = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(member2.getId(),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 2, 0, 0, 0),
                LocalDateTime.of(2023, 9, 1, 0, 0, 0),
                LocalDateTime.of(2023, 9, 30, 0, 0, 0), "subject3", "contents3");

        StudyGroup studyGroup1 = request.toCreateServiceParam().toEntityWithLeader(member2);
        StudyGroup studyGroup2 = request.toCreateServiceParam().toEntityWithLeader(member2);

        //when
        studyGroupList.add(studyGroup1);
        studyGroupList.add(studyGroup2);

        //then
        assertNotEquals(studyGroupList.size(), 1);
    }

    @DisplayName("스터디 그룹 수정 성공")
    @Test
    @Transactional
    void updateStudyGroupSuccess() {
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
        StudyGroup studyGroup = studyGroupRepository.findById(response.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        UpdateStudyGroupRequest request1 = updateStudyGroupRequest(
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 2, 0, 0, 0),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0, 0), "subject update", "contents update", "contents update", 15, 333);
        studyGroupService.updateStudyGroup(request1.toUpdateStudyGroupParam(studyGroup.getId()), authentication);

        //then
        assertThat(studyGroup.getId()).isNotNull();
        assertThat(studyGroup.getSubject()).isNotEqualTo(request.getSubject());
        assertThat(studyGroup.getContents()).isNotEqualTo(request.getContents());
        assertThat(studyGroup.getContentsDetail()).isNotEqualTo(request.getContentsDetail());
        assertThat(studyGroup.getStudyStartDt()).isNotEqualTo(request.getStudyStartDt());
        assertThat(studyGroup.getStudyEndDt()).isNotEqualTo(request.getStudyEndDt());
        assertThat(studyGroup.getMaxSize()).isNotEqualTo(request.getMaxSize());
        assertThat(studyGroup.getPrice()).isNotEqualTo(request.getPrice());
        assertThat(studyGroup.getRecruitmentStartDt()).isNotEqualTo(request.getRecruitmentStartDt());
        assertThat(studyGroup.getRecruitmentEndDt()).isNotEqualTo(request.getRecruitmentEndDt());
        assertThat(studyGroup.getLeader().getId()).isEqualTo(request.getMemberId());

    }

    @DisplayName("스터디 그룹 수정 실패 - 수정하려는 사용자와 스터디 그룹을 만든사람 정보가 다른 경우")
    @Test
    @Transactional
    void updateStudyGroupFailed() {
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
        StudyGroup studyGroup = studyGroupRepository.findById(response.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        //
        assertThat(studyGroup.getLeader().getEmail()).isNotEqualTo("다른 아이디");

    }

    @DisplayName("스터디 그룹 삭제 성공 (= 모집 취소)")
    @Test
    @Transactional
    void cancelSuccess() {
        //given

        Member member1 = createMember("스터디 리더", "dlaehdgus23@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(member1.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member1);
        studyGroupRepository.save(studyGroup);

        //when
        studyGroupService.deleteStudyGroup(studyGroup.getId(), authentication);

        //then
        assertThat(studyGroup.getStatus()).isEqualTo(StudyGroupStatus.RECRUIT_CANCEL);
    }

    @DisplayName("스터디 그룹 삭제 실패 - 스터디가 시작된 경우 삭제 불가 (현재 날짜 > 스터디 시작 날짜)")
    @Test
    @Transactional
    void cancelFailed() {
        //given

        Member member1 = createMember("스터디 리더", "dlaehdgus23@naver.com");
        memberRepository.save(member1);
        Authentication authentication = createAuthentication();

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(member1.getId(),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 2, 0, 0, 0),
                LocalDateTime.of(2023, 10, 1, 0, 0, 0),
                LocalDateTime.of(2023, 10, 30, 0, 0, 0), "subject", "contents");
        StudyGroup studyGroup = request.toCreateServiceParam().toEntityWithLeader(member1);
        studyGroupRepository.save(studyGroup);

        //when
        if (!studyGroup.isStarted()) {
            studyGroupService.deleteStudyGroup(studyGroup.getId(), authentication);
        }
        //then
        assertThat(studyGroup.getStatus()).isNotEqualTo(StudyGroupStatus.RECRUIT_CANCEL);
    }

    /**
     *
     * @param name
     * @param email
     * 회원가입 메서드
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
     *
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
                .price(25000L)
                .studyStartDt(studyStartDt)
                .studyEndDt(studyEndDt)
                .recruitmentStartDt(recruitmentStartDt)
                .recruitmentEndDt(recruitmentEndDt)
                .build();
    }

    /**
     *
     * @param studyStartDt
     * @param studyEndDt
     * @param recruitmentStartDt
     * @param recruitmentEndDt
     * @param subject
     * @param contents
     * @return
     */
    private UpdateStudyGroupRequest updateStudyGroupRequest(LocalDateTime studyStartDt, LocalDateTime studyEndDt, LocalDateTime recruitmentStartDt, LocalDateTime recruitmentEndDt, String subject, String contents, String contentsDetail, int maxSize, long price) {
        return UpdateStudyGroupRequest.builder()
                .subject(subject)
                .contents(contents)
                .contentsDetail(contentsDetail)
                .maxSize(maxSize)
                .price(price)
                .studyStartDt(studyStartDt)
                .studyEndDt(studyEndDt)
                .recruitmentStartDt(recruitmentStartDt)
                .recruitmentEndDt(recruitmentEndDt)
                .build();
    }
    /**
     *
     * 토큰 인증을 위한 인증 기능 메서드
     */
    private Authentication createAuthentication() {

        String email = "dlaehdgus23@naver.com";
        String password = "ehdgus1234";

        return new UsernamePasswordAuthenticationToken(email, password,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

}