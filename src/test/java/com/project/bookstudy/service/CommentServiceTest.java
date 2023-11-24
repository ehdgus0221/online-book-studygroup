package com.project.bookstudy.service;

import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.category.service.CategoryService;
import com.project.bookstudy.comment.domain.Comment;
import com.project.bookstudy.comment.dto.request.CreateCommentRequest;
import com.project.bookstudy.comment.dto.response.CommentResponse;
import com.project.bookstudy.comment.dto.response.CreateCommentResponse;
import com.project.bookstudy.comment.repository.CommentRepository;
import com.project.bookstudy.comment.service.CommentService;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.request.CreatePostRequest;
import com.project.bookstudy.post.dto.request.UpdatePostRequest;
import com.project.bookstudy.post.dto.response.CreatePostResponse;
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

@Slf4j
@SpringBootTest
public class CommentServiceTest {

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
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("댓글 생성 성공")
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

        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        Post findPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        //when
        CreateCommentRequest commentRequest = makeCreateCommentRequest(findPost.getId(), null, "댓글 작성");
        CreateCommentResponse commentResponse = commentService.createComment(commentRequest, authentication);
        Comment comment = commentRepository.findById(commentResponse.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COMMENT_NOT_FOUND.getDescription()));

        //then
        assertThat(comment.getContent()).isEqualTo(commentRequest.getContent());
    }

    @Test
    @Transactional
    @DisplayName("댓글 생성 실패 - 요청 시 작성한 부모 댓글(parentId)이 없는 경우")
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

        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        Post findPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        //when
        //then
        CreateCommentRequest commentRequest = makeCreateCommentRequest(findPost.getId(), 1L, "댓글 작성");

        assertThatThrownBy(() -> commentService.createComment(commentRequest, authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorCode.PARENT_NOT_FOUND.getDescription());
    }

    @Test
    @Transactional
    @DisplayName("댓글 조회 성공 - 부모 댓글 1개, 자식 댓글 2개 작성 후 자식 댓글 갯수 비교")
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

        CreatePostRequest postRequest = makeCreatePostRequest("게시글 만들기", "게시글 테스트", categoryId, studyGroup.getId());

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes()));

        CreatePostResponse createPost = postService.createPost(postRequest, multipartFiles, authentication);

        Post findPost = postRepository.findById(createPost.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        CreateCommentRequest commentRequest = makeCreateCommentRequest(findPost.getId(), null, "댓글 작성");
        CreateCommentResponse commentResponse = commentService.createComment(commentRequest, authentication);
        Comment comment = commentRepository.findById(commentResponse.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COMMENT_NOT_FOUND.getDescription()));
        CreateCommentRequest commentRequest2 = makeCreateCommentRequest(findPost.getId(), comment.getId(), "댓글 작성");
        CreateCommentResponse commentResponse2 = commentService.createComment(commentRequest2, authentication);

        CreateCommentRequest commentRequest3 = makeCreateCommentRequest(findPost.getId(), comment.getId(), "댓글 작성");
        CreateCommentResponse commentResponse3 = commentService.createComment(commentRequest3, authentication);

        //when
        Page<CommentResponse> commentResponsesPage = commentService.getRootOrChildCommentList(comment.getId(), PageRequest.of(0, 10));

        //then
        List<CommentResponse> commentResponses = commentResponsesPage.getContent();

        assertThat(commentResponses.size()).isEqualTo(2);
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

    public static CreateCommentRequest makeCreateCommentRequest(Long postId, Long parentId, String content) {
        return CreateCommentRequest.builder()
                .postId(postId)
                .parentId(parentId)
                .content(content)
                .build();
    }
}
