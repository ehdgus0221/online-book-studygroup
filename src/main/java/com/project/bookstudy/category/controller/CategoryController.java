package com.project.bookstudy.category.controller;

import com.project.bookstudy.category.dto.CategoryResponse;
import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.dto.CreateCategoryResponse;
import com.project.bookstudy.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<CreateCategoryResponse> createCategory(@RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getRootOrChildCategory(@RequestParam(required = false) Long parentId) {

        return ResponseEntity.ok(categoryService.getRootOrChildCategoryList(parentId));
    }
}
