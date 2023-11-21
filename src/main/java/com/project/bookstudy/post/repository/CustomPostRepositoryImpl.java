package com.project.bookstudy.post.repository;

import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.PostSearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;
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

    @Override
    public Page<Post> searchPost(Pageable pageable, PostSearchCond cond) {
        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .join(post.studyGroup, studyGroup).fetchJoin()
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .join(post.files, file).fetchJoin()
                .where(post.studyGroup.id.eq(cond.getStudyGroupId()),   //essential condition
                        categoryCond(cond.getCategoryId()),
                        subjectCond(cond.getSubject()),
                        contentsCond(cond.getContents()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(post.count())
                .from(post)
                .where(post.studyGroup.id.eq(cond.getStudyGroupId()),
                        categoryCond(cond.getCategoryId()),
                        subjectCond(cond.getSubject()),
                        contentsCond(cond.getContents()))
                .fetchOne();

        return new PageImpl(posts, pageable, total);
    }

    private BooleanExpression categoryCond(Long categoryId) {
        return categoryId != null ? post.category.id.eq(categoryId) : null;
    }
    private BooleanExpression subjectCond(String subject) {
        return StringUtils.hasText(subject) ? post.subject.contains(subject) : null;
    }

    private BooleanExpression contentsCond(String contents) {
        return StringUtils.hasText(contents) ? post.subject.contains(contents) : null;
    }
}
