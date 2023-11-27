package com.project.bookstudy.common.exception;

import com.project.bookstudy.common.dto.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileException extends RuntimeException {

    private final ErrorCode error;
    private final String message;
    private final HttpStatus httpStatus;

    public FileException(ErrorCode error) {
        this.error = error;
        this.message = error.getDescription();
        this.httpStatus = error.getHttpStatus();
    }

}
