package com.project.bookstudy.post.file.domain;

import com.project.bookstudy.common.domain.BaseTimeEntity;
import com.project.bookstudy.post.domain.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean isDeleted = Boolean.FALSE;

    @Builder
    private File(String filePath, Post post) {
        this.filePath = filePath;
        this.post = post;
    }

    public static File of(String filePath ,Post post) {
        File file = File.builder()
                .filePath(filePath)
                .post(post)
                .build();

        post.getFiles().add(file);
        return file;
    }

    public void deleteFile(Post post) {
        for (File file : post.getFiles()) {
            file.isDeleted = Boolean.TRUE;
        }
    }

}
