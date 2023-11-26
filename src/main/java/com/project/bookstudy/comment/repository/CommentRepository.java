package com.project.bookstudy.comment.repository;

import com.project.bookstudy.comment.domain.Comment;
import com.project.bookstudy.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
    List<Comment> findAllByPost(Post post);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Comment c set c.isDeleted = true where c.post in :post")
    void deleteAllInBatchByPostIn(@Param("post") List<Post> post);
}
