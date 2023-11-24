package com.project.bookstudy.post.controller;

import com.project.bookstudy.post.dto.request.CreatePostRequest;
import com.project.bookstudy.post.dto.request.UpdatePostRequest;
import com.project.bookstudy.post.dto.response.CreatePostResponse;
import com.project.bookstudy.post.dto.PostDto;
import com.project.bookstudy.post.dto.request.PostSearchCond;
import com.project.bookstudy.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    /**
     *
     * @param request
     * @param imageFiles
     * @param authentication
     * 게시글 생성
     */

    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(@RequestPart CreatePostRequest request,
                                                         @RequestPart(value = "files", required = false) List<MultipartFile> imageFiles,
                                                         Authentication authentication) {
        return ResponseEntity.ok(postService.createPost(request, imageFiles, authentication));
    }

    /**
     *
     * @param postId
     * 게시글 단일 조회
     */

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable("id") Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    /**
     *
     * @param pageable
     * @param cond
     * 게시글 전체 조회
     * cond를 통해 검색 조건 필터 가능
     * 1. studyGroupId (스터디그룹에 따라 보이는 항목이 다르므로 검색 조건에 필수)
     * 2. categoryId;
     * 3. subject;
     * 4. contents;
     */
    @GetMapping
    public ResponseEntity<Page<PostDto>> getPosts(@PageableDefault Pageable pageable,
                                  @ModelAttribute PostSearchCond cond) {
        return ResponseEntity.ok(postService.getPostList(pageable, cond));
    }

    /**
     *
     * @param postId
     * @param request
     * @param imageFiles
     * @param authentication
     * 게시글 수정
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable("id") Long postId,
            @RequestPart UpdatePostRequest request,
                           @RequestPart(value = "files", required = false) List<MultipartFile> imageFiles,
                           Authentication authentication) {
        postService.updatePost(postId, request, imageFiles, authentication);
        return ResponseEntity.ok().build();

    }

    /**
     *
     * @param postId
     * @param authentication
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId,
                                           Authentication authentication) {
        postService.deletePost(postId, authentication);
        return ResponseEntity.ok().build();
    }

}
