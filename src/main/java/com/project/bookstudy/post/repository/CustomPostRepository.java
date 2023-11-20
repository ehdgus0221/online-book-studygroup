package com.project.bookstudy.post.repository;

import com.project.bookstudy.post.domain.Post;

import java.util.Optional;

public interface CustomPostRepository {
    Optional<Post> findByIdWithAll(Long id);
}
