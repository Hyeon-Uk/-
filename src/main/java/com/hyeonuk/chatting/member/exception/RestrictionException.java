package com.hyeonuk.chatting.member.exception;

public class RestrictionException extends RuntimeException{
    public RestrictionException(String message) {
        super(message);
    }

    public RestrictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
