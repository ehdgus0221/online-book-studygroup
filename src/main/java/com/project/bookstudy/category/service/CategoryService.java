package com.project.bookstudy.category.service;

import com.project.bookstudy.category.domain.Category;
import com.project.bookstudy.category.dto.CategoryDto;
import com.project.bookstudy.category.dto.CategoryResponse;
import com.project.bookstudy.category.dto.CreateCategoryRequest;
import com.project.bookstudy.category.dto.CreateCategoryResponse;
import com.project.bookstudy.category.repository.CategoryRepository;
import com.project.bookstudy.common.dto.ErrorCode;
import com.project.bookstudy.study_group.domain.StudyGroup;
import com.project.bookstudy.study_group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StudyGroupRepository studyGroupRepository;

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
}
