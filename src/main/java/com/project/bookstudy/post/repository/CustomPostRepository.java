package com.project.bookstudy.post.repository;

import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.PostSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomPostRepository {
    Optional<Post> findByIdWithAll(Long id);
    Page<Post> searchPost(Pageable pageable, PostSearchCond cond);
}
