package com.hyeonuk.chatting.member.exception.auth.login;

public class RestrictionException extends RuntimeException{
    public RestrictionException(String message) {
        super(message);
    }

    public RestrictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
