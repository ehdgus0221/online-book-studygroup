package com.project.bookstudy.post.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.dto.CreatePostRequest;
import com.project.bookstudy.post.dto.CreatePostResponse;
import com.project.bookstudy.post.file.repository.FileRepository;
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
import java.util.ArrayList;
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
            List<String> fileUrls = new ArrayList<>();

            for (MultipartFile file : imageFiles) {
                uploadProfileImage(member, file);
            }

            // 저장된 파일 경로를 post 엔티티에 연결
            post.setFileUrls(fileUrls);
        }
        Post savePost = postRepository.save(post);


        return CreatePostResponse.fromPost(savePost);
    }

    /**
     * 파일업로드 메서드
     * @param member
     * @param imageFile
     * @throws IOException
     */
    private void uploadProfileImage(Member member, MultipartFile imageFile){
        try{
            member.setImage(
                    s3Uploader.uploadAndGenerateUrl(
                            imageFile,
                            s3BucketFolderName +
                                    RandomStringMaker.randomStringMaker())
            );
        } catch (IOException e){
            throw new FileException(Error.UPLOAD_IMAGE_FILE_FAILED);
        }
        memberRepository.save(member);
    }
}
