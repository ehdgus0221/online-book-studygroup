package com.project.bookstudy.post.repository;

import com.project.bookstudy.post.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.project.bookstudy.category.domain.QCategory.category;
import static com.project.bookstudy.member.domain.QMember.member;
import static com.project.bookstudy.post.domain.QPost.post;
import static com.project.bookstudy.post.file.domain.QFile.file;
import static com.project.bookstudy.study_group.domain.QStudyGroup.studyGroup;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Post> findByIdWithAll(Long id) {
        Post result = jpaQueryFactory.selectFrom(post)
                .join(post.studyGroup, studyGroup).fetchJoin()
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .leftJoin(post.files, file).fetchJoin()
                .where(post.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
