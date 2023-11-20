package com.project.bookstudy.post.dto;

import com.project.bookstudy.category.dto.CategoryDto;
import com.project.bookstudy.member.dto.MemberDto;
import com.project.bookstudy.post.domain.Post;
import com.project.bookstudy.post.file.dto.FileDto;
import com.project.bookstudy.study_group.dto.StudyGroupDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostDto {
    private Long id;
    private String subject;
    private String contents;
    private CategoryDto categoryDto;
    private MemberDto memberDto;
    private StudyGroupDto studyGroupDto;

    private List<FileDto> filePaths;

    @Builder(access = AccessLevel.PRIVATE)
    public PostDto(Long id, String subject, String contents, CategoryDto categoryDto, MemberDto memberDto, StudyGroupDto studyGroupDto, List<FileDto> filePaths) {
        this.id = id;
        this.subject = subject;
        this.contents = contents;
        this.categoryDto = categoryDto;
        this.memberDto = memberDto;
        this.studyGroupDto = studyGroupDto;
        this.filePaths = filePaths;
    }

    @Builder(access = AccessLevel.PRIVATE)


    public static PostDto fromEntity(Post post) {
        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .contents(post.getContents())
                .categoryDto(CategoryDto.fromEntity(post.getCategory()))
                .studyGroupDto(StudyGroupDto.fromEntity(post.getStudyGroup()))
                .filePaths(post.getFiles()
                        .stream()
                        .map(FileDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
        return postDto;
    }
}
