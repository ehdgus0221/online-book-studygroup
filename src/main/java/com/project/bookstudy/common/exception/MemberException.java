package com.project.bookstudy.common.exception;

import lombok.Getter;
import com.project.bookstudy.common.dto.ErrorCode;

@Getter
public class MemberException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public MemberException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
    }

}