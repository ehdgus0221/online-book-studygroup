package com.project.bookstudy.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ErrorResponse {

    private String code;
    private String message;
    private Map<String, List<String>> errorDetails; // 유효성 검사 여러개 발견될 경우 사용
    @Builder
    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}