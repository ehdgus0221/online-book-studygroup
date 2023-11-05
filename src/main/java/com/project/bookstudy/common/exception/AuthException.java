package com.project.bookstudy.common.exception;

import com.project.bookstudy.common.dto.Error;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final Error error;
    private final String description;

    public AuthException(Error error) {
        this.error = error;
        this.description = error.getDescription();
    }

}