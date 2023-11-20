package com.project.bookstudy.post.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.common.exception.FileException;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.CreatePostRequest;
import com.project.bookstudy.post.dto.CreatePostResponse;
import com.project.bookstudy.post.file.domain.File;
import com.project.bookstudy.post.file.repository.FileRepository;
import com.project.bookstudy.post.file.service.S3Deleter;
import com.project.bookstudy.post.file.service.S3Uploader;
import com.project.bookstudy.post.repository.PostRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import com.project.bookstudy.util.RandomStringMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
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

    /**
     * @param imageFile
     * @param post      파일 업로드 메서드
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
