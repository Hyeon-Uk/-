package com.hyeonuk.chatting.member.exception.control;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }

}
