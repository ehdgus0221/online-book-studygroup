package com.project.bookstudy.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.dto.CategoryResponse;
import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.dto.UpdateCategoryRequest;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.category.service.CategoryService;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.dto.response.CreateStudyGroupResponse;
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

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Slf4j
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getStudyGroupId())
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
    @DisplayName("부모 카테고리 생성 실패 - CreateCategoryRequest를 통해 만든 부모 카테고리와 다른 데이터를 넣어서 비교")
    void createParentCategoryFailed() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        CreateCategoryRequest categoryRequest = makeCreateCategoryRequest(null, studyGroup);

        //when
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();

        //then
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        assertThat(findCategory.getSubject()).isNotEqualTo("subject2");
        assertThat(findCategory.getStudyGroup().getId()).isNotEqualTo(categoryRequest.getStudyGroupId() + 1);
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
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

    @Test
    @Transactional
    @DisplayName("자식 카테고리 생성 실패 - CreateCategoryRequest를 통해 만든 자식 카테고리와 다른 데이터를 넣어서 비교")
    void createChildCategoryFailed() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
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

        assertThat(findCategory.getId()).isNotEqualTo(categoryId + 1);
        assertThat(findCategory.getSubject()).isNotEqualTo("subject2");
    }

    @Test
    @Transactional
    @DisplayName("카테고리 조회 성공")
    void getParentCategorySuccess() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        // 자식 카테고리 생성
        CreateCategoryRequest categoryRequest = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();

        //when
        CategoryResponse response = categoryService.getRootOrChildCategoryList(parentCategory.getId());

        //then
        assertThat(response.getCategoryId()).isEqualTo(parentCategory.getId());
        assertThat(response.getChildCategories().size()).isEqualTo(1);

    }

    @Test
    @Transactional
    @DisplayName("카테고리 조회 실패 - 자식 카테고리 2개 생성 후 size에 다른값을 넣어 비교")
    void getParentCategoryFailed() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        // 자식 카테고리 생성
        CreateCategoryRequest categoryRequest1 = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId1 = categoryService.createCategory(categoryRequest1).getCategoryId();

        CreateCategoryRequest categoryRequest2 = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId2 = categoryService.createCategory(categoryRequest2).getCategoryId();

        //when
        CategoryResponse response = categoryService.getRootOrChildCategoryList(parentCategory.getId());

        //then
        assertThat(response.getChildCategories().size()).isNotEqualTo(3);

    }

    @Test
    @Transactional
    @DisplayName("카테고리 수정 성공")
    void updateParentCategorySuccess() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        // 자식 카테고리 생성
        CreateCategoryRequest categoryRequest1 = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId1 = categoryService.createCategory(categoryRequest1).getCategoryId();

        CreateCategoryRequest categoryRequest2 = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId2 = categoryService.createCategory(categoryRequest2).getCategoryId();

        Category category = categoryRepository.findById(categoryId1)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        //when
        UpdateCategoryRequest updateCategoryRequest = makeUpdateCategoryRequest(categoryId2, "수정 완료1");
        categoryService.updateCategory(categoryId1, updateCategoryRequest);

        //then
        assertThat(updateCategoryRequest.getParentCategoryId()).isEqualTo(categoryId2);
        assertThat(updateCategoryRequest.getSubject()).isEqualTo("수정 완료1");

    }

    @Test
    @Transactional
    @DisplayName("카테고리 수정 실패 - 존재하지 않는 카테고리 id를 parentCategoryId로 입력하는 경우")
    void updateParentCategoryFailed() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        // 자식 카테고리 생성
        CreateCategoryRequest categoryRequest1 = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId1 = categoryService.createCategory(categoryRequest1).getCategoryId();

        CreateCategoryRequest categoryRequest2 = makeCreateCategoryRequest(parentCategory.getId(), studyGroup);
        Long categoryId2 = categoryService.createCategory(categoryRequest2).getCategoryId();

        Category category = categoryRepository.findById(categoryId1)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        //when
        //then
        UpdateCategoryRequest updateCategoryRequest = makeUpdateCategoryRequest(categoryId2 + 10, "수정 완료1");
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId1, updateCategoryRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getDescription());
    }

    @Test
    @Transactional
    @DisplayName("카테고리 삭제 성공")
    void deleteParentCategorySuccess() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        //when
        categoryService.deleteCategory(parentCategory.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<Category> deletedCategory = categoryRepository.findById(parentCategory.getId());
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    @Transactional
    @DisplayName("카테고리 삭제 실패 - 카테고리 id가 조회되지 않는 경우")
    void deleteParentCategoryFailed() {
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
        CreateStudyGroupResponse response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getLeaderId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));

        // 부모 카테고리 생성
        Category parentCategory = categoryRepository.save(Category.from(null, studyGroup, "부모카테고리"));

        //when
        //then
        assertThatThrownBy(() -> categoryService.deleteCategory(parentCategory.getId() + 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getDescription());
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

    public static UpdateCategoryRequest makeUpdateCategoryRequest(Long parentId, String subject) {
        return UpdateCategoryRequest.builder()
                .parentCategoryId(parentId)
                .subject(subject)
                .build();
    }
}
