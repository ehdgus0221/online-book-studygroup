package com.project.bookstudy.service;

import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.category.service.CategoryService;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.MemberException;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.request.CreatePostRequest;
import com.project.bookstudy.post.dto.request.UpdatePostRequest;
import com.project.bookstudy.post.dto.response.CreatePostResponse;
import com.project.bookstudy.post.dto.PostDto;
import com.project.bookstudy.post.dto.request.PostSearchCond;
import com.project.bookstudy.post.file.domain.File;
import com.project.bookstudy.post.file.repository.FileRepository;
import com.project.bookstudy.post.repository.PostRepository;
import com.project.bookstudy.post.service.PostService;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import com.project.bookstudy.study_group.dto.request.CreateStudyGroupRequest;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.study_group.service.StudyGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
@SpringBootTest
public class PostServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    StudyGroupRepository studyGroupRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;
    @Autowired
    StudyGroupService studyGroupService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("게시글 생성 성공")
    void createPostSuccess() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        //when
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);
        //then
        Post findPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        assertThat(findPost.getSubject()).isEqualTo(postRequest.getSubject());
        assertThat(findPost.getContents()).isEqualTo(postRequest.getContents());
        assertThat(findPost.getStudyGroup().getId()).isEqualTo(postRequest.getStudyGroupId());
        assertThat(findPost.getCategory().getId()).isEqualTo(postRequest.getCategoryId());
    }

    @Test
    @Transactional
    @DisplayName("게시글 생성 실패 - 스터디그룹 없는 경우")
    void createPostFailed() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        //when
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId() + 1);

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        //then
        assertThatThrownBy(() -> postService.createPost(postRequest, multipartFiles, authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorCode.STUDY_GROUP_NOT_FOUND.getDescription());
    }

    @Test
    @Transactional
    @DisplayName("게시글 단일 조회 성공")
    void getPostSuccess() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        //when
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        PostDto postDto = postService.getPost(createPost.getPostId());
        //then
        Post findPost = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        assertThat(findPost.getSubject()).isEqualTo(postRequest.getSubject());
        assertThat(findPost.getContents()).isEqualTo(postRequest.getContents());
        assertThat(findPost.getStudyGroup().getId()).isEqualTo(postRequest.getStudyGroupId());
        assertThat(findPost.getCategory().getId()).isEqualTo(postRequest.getCategoryId());
    }

    @Test
    @Transactional
    @DisplayName("게시글 단일 조회 실패 - 게시글 생성 시 입력한 데이터와 다른 데이터를 삽입하여 비교")
    void getPostFailed() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        //when
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        PostDto postDto = postService.getPost(createPost.getPostId());
        //then
        Post findPost = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        assertThat(findPost.getSubject()).isNotEqualTo(postRequest.getSubject() + "a");
        assertThat(findPost.getContents()).isNotEqualTo(postRequest.getContents() + "a");
        assertThat(findPost.getStudyGroup().getId()).isNotEqualTo(postRequest.getStudyGroupId() + 1);
        assertThat(findPost.getCategory().getId()).isNotEqualTo(postRequest.getCategoryId() + 1);
    }

    @Test
    @Transactional
    @DisplayName("게시글 전체 조회 성공")
    void getPostListSuccess() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        //when
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost1 = postService.createPost(postRequest, multipartFiles, authentication);
        CreatePostResponse createPost2 = postService.createPost(postRequest, multipartFiles, authentication);


        Pageable pageable = PageRequest.of(0, 10); // 예시로 첫 페이지, 한 페이지당 10개씩
        PostSearchCond cond = PostSearchCond.builder().studyGroupId(studyGroup.getId()).build();

        //then
        Page<PostDto> postDtoPage = postService.getPostList(pageable, cond);

        assertEquals(postDtoPage.getTotalElements(), 2);
    }

    @Test
    @Transactional
    @DisplayName("게시글 전체 조회 실패 - 전체 게시물 수가 맞지 않는 경우")
    void getPostListFailed() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        //when
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost1 = postService.createPost(postRequest, multipartFiles, authentication);
        CreatePostResponse createPost2 = postService.createPost(postRequest, multipartFiles, authentication);


        Pageable pageable = PageRequest.of(0, 10); // 예시로 첫 페이지, 한 페이지당 10개씩
        PostSearchCond cond = PostSearchCond.builder().studyGroupId(studyGroup.getId()).build();

        //then
        Page<PostDto> postDtoPage = postService.getPostList(pageable, cond);

        assertNotEquals(postDtoPage.getTotalElements(), 1);
    }

    @Test
    @Transactional
    @DisplayName("게시글 수정 성공")
    void updatePostSuccess() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());
        List<MultipartFile> multipartFiles1 = new ArrayList<>();
        multipartFiles1.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));
        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles1, authentication);
        //when
        List<MultipartFile> multipartFiles2 = new ArrayList<>();
        multipartFiles2.add(new MockMultipartFile("files", "file2.txt", "text/plain", "File 2 content".getBytes()));
        UpdatePostRequest updatePostRequest = makeUpdatePostRequest(categoryId, "제목 수정", "내용 수정");
        postService.updatePost(createPost.getPostId(), updatePostRequest, multipartFiles2, authentication);

        Post resultPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        //then
        assertThat(resultPost.getSubject()).isEqualTo("제목 수정");
        assertThat(resultPost.getContents()).isEqualTo("내용 수정");
        assertThat(resultPost.getContents()).isEqualTo("내용 수정");
    }

    @Test
    @Transactional
    @DisplayName("게시글 수정 실패 - 수정한 내용과 다른 데이터를 대입해서 비교")
    void updatePostFailed() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();
        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());
        List<MultipartFile> multipartFiles1 = new ArrayList<>();
        multipartFiles1.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));
        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles1, authentication);
        //when
        List<MultipartFile> multipartFiles2 = new ArrayList<>();
        multipartFiles2.add(new MockMultipartFile("files", "file2.txt", "text/plain", "File 2 content".getBytes()));
        UpdatePostRequest updatePostRequest = makeUpdatePostRequest(categoryId, "제목 수정", "내용 수정");
        postService.updatePost(createPost.getPostId(), updatePostRequest, multipartFiles2, authentication);

        Post resultPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        //then
        assertThat(resultPost.getSubject()).isNotEqualTo("제목 수정1");
        assertThat(resultPost.getContents()).isNotEqualTo("내용 수정1");
        assertThat(resultPost.getContents()).isNotEqualTo("내용 수정1");
    }

    @Test
    @Transactional
    @DisplayName("게시글 삭제 성공")
    void deletePostSuccess() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

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
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();

        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        //when
        postService.deletePost(createPost.getPostId(), authentication);
        //then

        Post findPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));
        List<File> findFile = fileRepository.findAllByPost(findPost);
        assertThat(findFile)
                .as("All files should be deleted.")
                .allMatch(file -> file.getIsDeleted().equals(Boolean.TRUE));

        assertThat(findPost.getIsDeleted()).isEqualTo(Boolean.TRUE);

    }

    @Test
    @Transactional
    @DisplayName("게시글 삭제 실패 - 작성자가 아닌 다른 사람이 삭제 하는 경우")
    void deletePostFailed() throws IOException {
        //given
        Member member = createMember("member", "member@naver.com");
        memberRepository.save(member);
        Member leader = createMember("leader", "leader@naver.com");
        memberRepository.save(leader);

        Authentication authentication = createAuthenticationMember();
        Authentication authentication2 = createAuthenticationLeader();

        CreateStudyGroupRequest request = createStudyCreateGroupRequest(leader.getId(),
                LocalDateTime.of(2023, 12, 1, 0, 0, 0),
                LocalDateTime.of(2023, 12, 2, 0, 0, 0),
                LocalDateTime.of(2023, 11, 1, 0, 0, 0),
                LocalDateTime.of(2023, 11, 30, 0, 0, 0), "subject", "contents");
        StudyGroupDto response1 = studyGroupService.createStudyGroup(authentication, request.toStudyGroupParam());
        StudyGroup studyGroup = studyGroupRepository.findById(response1.getId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 없음"));
        CreateCategoryRequest categoryRequest = makeCreateCategoryRequest(null, studyGroup);
        Long categoryId = categoryService.createCategory(categoryRequest).getCategoryId();

        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        //when
        //then
        assertThatThrownBy(() -> postService.deletePost(createPost.getPostId(), authentication2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorCode.POST_DELETE_FAIL.getDescription());
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

    public static CreatePostRequest makeCreatePostRequest(String subject, String contents, Long categoryId, Long studyGroupId) {
        return CreatePostRequest.builder()
                .subject(subject)
                .contents(contents)
                .categoryId(categoryId)
                .studyGroupId(studyGroupId)
                .build();
    }

    public static UpdatePostRequest makeUpdatePostRequest(Long categoryId, String subject, String contents) {
        return UpdatePostRequest.builder()
                .categoryId(categoryId)
                .subject(subject)
                .contents(contents)
                .build();
    }

}
