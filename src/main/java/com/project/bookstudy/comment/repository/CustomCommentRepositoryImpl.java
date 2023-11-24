package com.project.bookstudy.comment.repository;

import com.project.bookstudy.comment.domain.Comment;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.project.bookstudy.comment.domain.QComment.comment;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Comment> findRootOrChildByParentId(Long id, Pageable pageable) {

        QueryResults<Comment> queryResults = jpaQueryFactory.selectFrom(comment)
                .where(getRootOrChild(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    public List<Comment> findRootOrChildByParentIdInDelete(Long id) {

        List<Comment> childCommentList = jpaQueryFactory.selectFrom(comment)
                .where(getRootOrChild(id))
                .fetch();

        return childCommentList;
    }

    private BooleanExpression getRootOrChild(Long id) {
        return id != null ? comment.parent.id.eq(id) : null;
    }
}
