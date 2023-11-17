package com.project.bookstudy.category.controller;

import com.project.bookstudy.category.dto.*;
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
    public ResponseEntity<CategoryResponse> getRootOrChildCategory(@RequestParam(required = false) Long parentId) {
        CategoryResponse response = categoryService.getRootOrChildCategoryList(parentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable("id") Long categoryId, @RequestBody UpdateCategoryRequest request) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok().build();
    }
}
