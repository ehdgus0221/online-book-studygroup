package com.project.bookstudy.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(@RequestPart CreatePostRequest request,
                                                         @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        return ResponseEntity.ok(postService.createPost(request,imageFile));
    }
}
