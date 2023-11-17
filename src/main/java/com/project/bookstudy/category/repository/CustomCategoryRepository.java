package com.project.bookstudy.category.repository;

import com.project.bookstudy.category.domain.Category;

import java.util.List;

public interface CustomCategoryRepository {
    List<Category> findRootOrChildByParentId(Long id);
}
