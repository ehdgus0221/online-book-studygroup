package com.project.bookstudy.comment.controller;

import com.project.bookstudy.comment.dto.request.CreateCommentRequest;
import com.project.bookstudy.comment.dto.response.CreateCommentResponse;
import com.project.bookstudy.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping
    public ResponseEntity<CommentResponse> getRootOrChildComment(@RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(commentService.getRootOrChildCommentList(parentId));
    }
}
