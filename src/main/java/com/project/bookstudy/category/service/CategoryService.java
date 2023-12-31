package com.project.bookstudy.category.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.dto.*;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.comment.repository.CommentRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.file.repository.FileRepository;
import com.project.bookstudy.post.repository.PostRepository;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    @Transactional
    public CreateCategoryResponse createCategory(CreateCategoryRequest request) {
        StudyGroup studyGroup = studyGroupRepository.findById(request.getStudyGroupId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.STUDY_GROUP_NOT_FOUND.getDescription()));

        Long parentId = request.getParentCategoryId();
        Category parentCategory = parentId != null ?
                categoryRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COMMENT_NOT_FOUND.getDescription()))
                : null;

        Category category = Category.from(parentCategory, studyGroup, request.getSubject());
        categoryRepository.save(category);

        return CreateCategoryResponse.fromCategory(category);
    }

    //null 입력시 root Category List 반환
    public CategoryResponse getRootOrChildCategoryList(@Nullable Long parentId) {
        List<Category> rootOrChildCategories = categoryRepository.findRootOrChildByParentId(parentId);

        List<CategoryDto> categoryDtoList = rootOrChildCategories
                .stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .categoryId(parentId)
                .childCategories(categoryDtoList)
                .build();
    }

    @Transactional
    public void updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        category.update(request.getSubject(), toUpdateParentCategory(request));
    }

    private Category toUpdateParentCategory(UpdateCategoryRequest request) {
        if (request.getParentCategoryId() == null) {
            return null;
        }

        Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        return parentCategory;
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CATEGORY_NOT_FOUND.getDescription()));

        deleteRelatedData(category);
    }

    private void deleteRelatedData(Category category) {

        if (category == null) return;

        List<Category> childCategories = categoryRepository.findRootOrChildByParentId(category.getId());
        for (Category childCategory : childCategories) {
            deleteRelatedData(childCategory);
        }

        List<Post> postList = postRepository.findPostsByCategory(category);

        //게시판 파일 삭제
        fileRepository.deleteAllInBatchByPostIn(postList);
        //게시판 댓글 삭제
        commentRepository.deleteAllInBatchByPostIn(postList);
        //카테고리의 게시판 삭제
        postRepository.softDeleteAllByCategory(category);
        //카테고리 삭제
        categoryRepository.delete(category);
    }
}
