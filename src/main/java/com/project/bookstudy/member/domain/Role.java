package com.project.bookstudy.member.domain;

public enum Role {
    MEMBER("ROLE_MEMBER")
    , MENTOR("ROLE_MENTOR")
    , ADMIN("ROLE_ADMIN");

    public final String ROLE_TEXT;
    private Role(String ROLE_TEXT) {
        this.ROLE_TEXT = ROLE_TEXT;
    }
}