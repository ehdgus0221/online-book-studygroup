package com.project.bookstudy.post.repository;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
    List<Post> findPostsByCategory(Category category);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.isDeleted = true where p.category = :category")
    int softDeleteAllByCategory(@Param("category") Category category);
}
