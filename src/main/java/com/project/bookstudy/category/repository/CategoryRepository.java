package com.project.bookstudy.category.repository;

import com.project.bookstudy.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
