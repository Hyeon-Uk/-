package com.hyeonuk.chatting.member.exception.auth.login;

public class InfoNotMatchException extends RuntimeException{
    public InfoNotMatchException(String message) {
        super(message);
    }

    public InfoNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
