package com.project.bookstudy.post.file.repository;

import com.project.bookstudy.post.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
