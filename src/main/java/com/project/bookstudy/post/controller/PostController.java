package com.project.bookstudy.post.controller;

import com.project.bookstudy.post.dto.CreatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(@RequestPart CreatePostRequest request,
                                                         @RequestPart(value = "files", required = false) List<MultipartFile> imageFiles) {
        return ResponseEntity.ok(postService.createPost(request,imageFiles));
    }
}
