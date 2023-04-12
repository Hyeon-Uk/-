package com.hyeonuk.chatting.member.exception.auth.join;

public class AlreadyExistException extends RuntimeException{
    public AlreadyExistException(String message) {
        super(message);
    }

}
