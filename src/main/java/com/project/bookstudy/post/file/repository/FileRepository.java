package com.project.bookstudy.post.file.repository;

import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByPost(Post post);

    @Transactional
    void deleteAllByPostIn(List<Post> posts);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update File f set f.isDeleted = true where f.post in :post")
    void deleteAllInBatchByPostIn(@Param("post") List<Post> post);
}
