package com.project.bookstudy.common.exception;

import lombok.Getter;
import com.project.bookstudy.common.dto.Error;

@Getter
public class MemberException extends RuntimeException {

    private final Error error;
    private final String message;

    public MemberException(Error error) {
        this.error = error;
        this.message = error.getDescription();
    }

}