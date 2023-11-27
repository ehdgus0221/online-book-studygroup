package com.project.bookstudy.util;

import java.util.UUID;

public class RandomStringMaker {
    public static String randomStringMaker(){
        return  UUID.randomUUID().toString();
    }
}
