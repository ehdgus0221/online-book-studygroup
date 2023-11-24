package com.project.bookstudy.comment.service;

import com.project.bookstudy.comment.domain.Comment;
import com.project.bookstudy.comment.domain.param.CreateCommentParam;
import com.project.bookstudy.comment.domain.param.UpdateCommentParam;
import com.project.bookstudy.comment.dto.CommentDto;
import com.project.bookstudy.comment.dto.request.CreateCommentRequest;
import com.project.bookstudy.comment.dto.response.CommentResponse;
import com.project.bookstudy.comment.dto.response.CreateCommentResponse;
import com.project.bookstudy.comment.repository.CommentRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreateCommentResponse createComment(CreateCommentRequest request
            , Authentication authentication) {

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        Long parentId = request.getParentId();
        Comment parentComment = parentId != null ?
                commentRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.PARENT_NOT_FOUND.getDescription()))
                : null;


        Comment savedComment = commentRepository.save(Comment.from(post, member, parentComment, request.toCommentParam()));
        CommentDto commentDto = CommentDto.fromEntity(savedComment);

        return CreateCommentResponse.builder()
                .commentId(commentDto.getId())
                .build();
    }

    public Page<CommentResponse> getRootOrChildCommentList(@Nullable Long parentId, Pageable pageable) {
        Page<Comment> rootOrChildComments = commentRepository.findRootOrChildByParentId(parentId, pageable);
        return rootOrChildComments.map(comment -> CommentResponse.builder()
                .commentId(comment.getId())
                .childComments(comment.getChildren().stream()
                        .map(CommentDto::fromEntity)
                        .collect(Collectors.toList()))
                .build());
    }

    @Transactional
    public void updateComment(Long commentId, UpdateCommentParam updateParam) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COMMENT_NOT_FOUND.getDescription()));

        comment.update(updateParam);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COMMENT_NOT_FOUND.getDescription()));
        if (comment.getIsDeleted()) {
            throw new IllegalArgumentException(ErrorCode.COMMENT_ALREADY_DELETED.getDescription());
        }
        //comment.delete();
        deleteChildCommentAndParentComment(comment);
    }

    private void deleteChildCommentAndParentComment(Comment comment) {

        if (comment == null) {
            return;
        }
        List<Comment> childComments = commentRepository.findRootOrChildByParentIdInDelete(comment.getId());
        for (Comment childComment : childComments) {
            deleteChildCommentAndParentComment(childComment);
        }

        commentRepository.delete(comment);
    }
}
