package com.project.bookstudy.comment.controller;

import com.project.bookstudy.comment.domain.Comment;
import com.project.bookstudy.comment.dto.request.CreateCommentRequest;
import com.project.bookstudy.comment.dto.response.CommentResponse;
import com.project.bookstudy.comment.dto.response.CreateCommentResponse;
import com.project.bookstudy.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    /**
     *
     * @param request
     * @param authentication
     * 댓글 작성
     */
    @PostMapping
    public ResponseEntity<CreateCommentResponse> createComment(@RequestBody CreateCommentRequest request
    , Authentication authentication) {
        return ResponseEntity.ok(commentService.createComment(request, authentication));
    }

    /**
     *
     * @param parentId
     * @param pageable
     * 댓글 조회
     */
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getRootOrChildComment(@RequestParam(required = false) Long parentId
    ,Pageable pageable) {
        return ResponseEntity.ok(commentService.getRootOrChildCommentList(parentId, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateComment(@PathVariable("id") Long commentId, @RequestBody UpdateCommentRequest request) {
        commentService.updateComment(commentId, request.getUpdateCommentparam());
        return ResponseEntity.ok().build();
    }
}
