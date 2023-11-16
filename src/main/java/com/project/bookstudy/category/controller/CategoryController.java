package com.project.bookstudy.category.controller;

import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.dto.CreateCategoryResponse;
import com.project.bookstudy.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<CreateCategoryResponse> createCategory(@RequestBody CreateCategoryRequest request) {
        Long categoryId = categoryService.createCategory(request);

        return ResponseEntity.ok(CreateCategoryResponse(categoryId));
    }
}
