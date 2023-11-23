package com.project.bookstudy.post.domain;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.common.domain.BaseTimeEntity;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.post.file.domain.File;
import com.project.bookstudy.study_group.domain.StudyGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE post_id = ?")
@Where(clause = "is_deleted = false")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String subject;

    @Lob
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id")
    private StudyGroup studyGroup;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    private Boolean isDeleted = Boolean.FALSE;

    @Builder(access = AccessLevel.PRIVATE)
    private Post(String subject, String contents, Category category, Member member, StudyGroup studyGroup) {
        this.subject = subject;
        this.contents = contents;
        this.category = category;
        this.member = member;
        this.studyGroup = studyGroup;
    }

    public static Post of(String contents, String subject,
                          StudyGroup studyGroup, Member member, Category category) {
        Post post = Post.builder()
                .member(member)
                .studyGroup(studyGroup)
                .category(category)
                .contents(contents)
                .subject(subject)
                .build();
        return post;
    }

    public void update(Category category, String newSubject, String newContents) {
        this.subject = newSubject;
        this.contents = newContents;
        this.category = category;
    }

    public void delete() {
        isDeleted = Boolean.FALSE;
    }

}
