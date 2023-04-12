package com.hyeonuk.chatting.member.exception.auth.join;

public class PasswordNotMatchException extends RuntimeException{
    public PasswordNotMatchException(String message) {
        super(message);
    }

}
