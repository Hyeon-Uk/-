package com.hyeonuk.chatting.integ.controller;

import com.hyeonuk.chatting.integ.util.ApiUtils;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.control.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionController {

    @ExceptionHandler({IllegalArgumentException.class, AlreadyExistException.class, UserNotFoundException.class})
    public ResponseEntity<ApiUtils.ApiResult<?>> BadRequestExceptionController(Exception e){
        return ApiUtils.error(e.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
