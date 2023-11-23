package com.project.bookstudy.post.file.repository;

import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByPost(Post post);
}
