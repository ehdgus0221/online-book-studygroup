package com.project.bookstudy.comment.domain;

import com.project.bookstudy.comment.domain.param.CreateCommentParam;
import com.project.bookstudy.comment.domain.param.UpdateCommentParam;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.post.domain.Post;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"childComment", "parentComment"})
@SQLDelete(sql = "UPDATE comment SET is_deleted = true WHERE comment_id = ?")
@Where(clause = "is_deleted = false")
@EqualsAndHashCode(of = {"id"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private Long id;


    private String content;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    private Boolean isDeleted = Boolean.FALSE;

    @Builder
    public Comment(String content, Post post, Member writer, Comment parent, boolean isDeleted) {
        this.content = content;
        this.regDt = LocalDateTime.now();
        this.udtDt = LocalDateTime.now();
        this.post = post;
        this.writer = writer;
        this.parent = parent;
        this.isDeleted = isDeleted;
    }

    public static Comment from(Post post, Member writer, Comment parent, CreateCommentParam commentParam) {
        Comment comment =  Comment.builder()
                .post(post)
                .writer(writer)
                .parent(parent)
                .content(commentParam.getContent())
                .build();

        if (parent != null) {
            parent.getChildren().add(comment);
        }
        return comment;
    }

    public void update(UpdateCommentParam param) {

        this.content = param.getContent();
        this.udtDt = LocalDateTime.now();
    }

}
