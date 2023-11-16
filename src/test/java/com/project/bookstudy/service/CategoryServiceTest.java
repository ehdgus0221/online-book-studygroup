package com.project.bookstudy.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.category.service.CategoryService;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;

    @Autowired
    StudyGroupService studyGroupService;

    @Autowired
    StudyGroupRepository studyGroupRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("부모 카테고리 생성 성공")
    void createParentCategorySuccess() {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(member);

        Authentication authentication = createAuthenticationMember();

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        StudyGroupDto response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        CreateCategoryRequest categoryRequest = makeCreateCategoryRequest(null, studyGroup);

        //when
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        assertThat(findCategory).isNotNull();
        assertThat(findCategory.getParentCategory()).isNull();
        assertThat(findCategory.getSubject()).isEqualTo(categoryRequest.getSubject());
        assertThat(findCategory.getStudyGroup().getId()).isEqualTo(categoryRequest.getStudyGroupId());
    }

    @Test
    @Transactional
    @DisplayName("자식 카테고리 생성 성공")
    void createChildCategorySuccess() {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(member);

        Authentication authentication = createAuthenticationMember();

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        StudyGroupDto response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        // 자식 카테고리 생성
        CreateCategoryRequest categoryRequest = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);


        //when
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        assertThat(findCategory.getId()).isEqualTo(categoryId);
        assertThat(findCategory.getParentCategory().getId()).isEqualTo(parentCategory.getId());
        assertThat(findCategory.getSubject()).isEqualTo(categoryRequest.getSubject());
        assertThat(findCategory.getIsDeleted()).isEqualTo(Boolean.FALSE);
        assertThat(parentCategory.getChildCategories().contains(findCategory)).isTrue();
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

    public static CreateCategoryRequest makeCreateCategoryRequest(Long parentId, StudyGroup studyGroup) {
        return CreateCategoryRequest.builder()
                .parentCategoryId(parentId)
                .subject("subject")
                .studyGroupId(studyGroup.getId())
                .build();
    }
}