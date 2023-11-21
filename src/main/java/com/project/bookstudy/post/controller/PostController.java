package com.project.bookstudy.post.controller;

import com.project.bookstudy.post.dto.CreatePostRequest;
import com.project.bookstudy.post.dto.CreatePostResponse;
import com.project.bookstudy.post.dto.PostDto;
import com.project.bookstudy.post.dto.PostSearchCond;
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

    @PatchMapping
    public ResponseEntity<Void> updatePost(@RequestPart UpdatePostRequest request,
                           @RequestPart(value = "files", required = false) List<MultipartFile> imageFiles,
                           Authentication authentication) {
        ResponseEntity.ok(postService.updatePost(request, imageFiles, authentication));
    }

}
