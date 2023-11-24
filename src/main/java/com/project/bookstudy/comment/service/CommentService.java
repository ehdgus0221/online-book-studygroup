package com.project.bookstudy.comment.service;

import com.project.bookstudy.comment.domain.Comment;
import com.project.bookstudy.comment.domain.param.CreateCommentParam;
import com.project.bookstudy.comment.dto.response.CreateCommentResponse;
import com.project.bookstudy.comment.repository.CommentRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CreateCommentResponse createComment(Long postId, Long parentId, CreateCommentParam commentParam, Authentication authentication) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));

        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        Comment comment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.PARENT_NOT_FOUND.getDescription()));

        Comment savedComment = commentRepository.save(Comment.from(post, member, comment, commentParam));
        CommentDto commentDto = CommentDto.fromEntity(savedComment);
        return CreateCommentResponse.builder()
                .commentId(commentDto.getId())
                .build();
    }

}
