package com.project.bookstudy.post.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.comment.repository.CommentRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.FileException;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.request.CreatePostRequest;
import com.project.bookstudy.post.dto.request.UpdatePostRequest;
import com.project.bookstudy.post.dto.response.CreatePostResponse;
import com.project.bookstudy.post.dto.PostDto;
import com.project.bookstudy.post.dto.request.PostSearchCond;
import com.project.bookstudy.post.file.domain.File;
import com.project.bookstudy.post.file.repository.FileRepository;
import com.project.bookstudy.post.file.service.S3Deleter;
import com.project.bookstudy.post.file.service.S3Uploader;
import com.project.bookstudy.post.repository.PostRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.util.RandomStringMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final S3Uploader s3Uploader;
    private final S3Deleter s3Deleter;

    private final String s3BucketFolderName = "profile-images/";

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request, List<MultipartFile> imageFiles,
                                         Authentication authentication) {
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.STUDY_GROUP_NOT_FOUND.getDescription()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        Post post = Post.of(request.getContents(), request.getSubject(), studyGroup, member, category);
        if (imageFiles.size() != 0) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    uploadProfileImage(file, post);
                }
            }

        }
        Post savePost = postRepository.save(post);

        return CreatePostResponse.fromPost(savePost);
    }

    public PostDto getPost(Long postId) {
        Post post = postRepository.findByIdWithAll(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));
        return PostDto.fromEntity(post);
    }

    public Page<PostDto> getPostList(Pageable pageable, PostSearchCond cond) {
        return postRepository.searchPost(pageable, cond)
                .map(PostDto::fromEntity);
    }

    @Transactional
    public void updatePost(Long postId,UpdatePostRequest request, List<MultipartFile> imageFiles,
                           Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        if (post.getMember().getId() != member.getId()) {
            throw new IllegalArgumentException(ErrorCode.POST_UPDATE_FAIL.getDescription());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        List<File> beforeFiles = fileRepository.findAllByPost(post);
        deleteBeforeFiles(beforeFiles, post);

        if (imageFiles.size() != 0) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    uploadProfileImage(file, post);
                }
            }
        }

        post.update(category, request.getSubject(), request.getContents());
    }


    @Transactional
    public void deletePost(Long postId, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POST_NOT_FOUND.getDescription()));
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getDescription()));

        if (post.getMember().getId() != member.getId()) {
            throw new IllegalArgumentException(ErrorCode.POST_DELETE_FAIL.getDescription());
        }

        // 파일 삭제
        fileRepository.deleteAllInBatchByPostIn(List.of(post));
        //게시판 댓글 삭제
        commentRepository.deleteAllInBatchByPostIn(List.of(post));
        postRepository.delete(post);

        post.delete();
    }

    /**
     *
     * @param beforeFiles
     * @param post
     * 파일 삭제 메서드
     */
    private void deleteBeforeFiles(List<File> beforeFiles, Post post) {
        for (File file : beforeFiles) {
            file.deleteFile(post);
        }
    }

    /**
     * @param imageFile
     * @param post
     * 파일 업로드 메서드
     */
    private void uploadProfileImage(MultipartFile imageFile, Post post) {
        try {
            File.of(s3Uploader.uploadAndGenerateUrl(
                    imageFile,
                    s3BucketFolderName + RandomStringMaker.randomStringMaker()), post);
        } catch (IOException e) {
            throw new FileException(ErrorCode.UPLOAD_IMAGE_FILE_FAILED);
        }
        postRepository.save(post);
    }


}
