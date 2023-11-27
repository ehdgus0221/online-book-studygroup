package com.project.bookstudy.common.exception;

import com.project.bookstudy.common.dto.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String description;

    public AuthException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

}