package com.project.bookstudy.post.file.dto;

import com.project.bookstudy.post.file.domain.File;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileDto {
    private Long id;
    private String path;

    public static FileDto fromEntity(File file) {
        return new FileDto(file.getId(), file.getFilePath());
    }
}
