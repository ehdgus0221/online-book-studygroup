package com.project.bookstudy.post.dto.response;

import com.project.bookstudy.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePostResponse {
    private Long postId;
    private String subject;
    private String contents;

    @Builder
    private CreatePostResponse(Long postId, String subject, String contents) {
        this.postId = postId;
        this.subject = subject;
        this.contents = contents;
    }

    public static CreatePostResponse fromPost(Post post) {
        return CreatePostResponse.builder()
                .postId(post.getId())
                .subject(post.getSubject())
                .contents(post.getContents())
                .build();
    }
}
