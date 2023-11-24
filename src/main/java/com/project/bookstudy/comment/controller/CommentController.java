package com.project.bookstudy.comment.controller;

import com.project.bookstudy.comment.dto.request.CreateCommentRequest;
import com.project.bookstudy.comment.dto.response.CreateCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CreateCommentResponse> createComment(@RequestBody CreateCommentRequest request
    , Authentication authentication) {
        return ResponseEntity.ok(commentService.createComment(request));
    }
}
