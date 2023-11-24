package com.project.bookstudy.comment.repository;

import com.project.bookstudy.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCommentRepository {
    Page<Comment> findRootOrChildByParentId(Long id, Pageable pageable);

}
