package com.hyeonuk.chatting.integ.controller;

import com.hyeonuk.chatting.integ.util.ApiUtils;
import com.hyeonuk.chatting.member.exception.AlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionController {

    @ExceptionHandler({IllegalArgumentException.class, AlreadyExistException.class})
    public ApiUtils.ApiResult BadRequestExceptionController(Exception e){
        return ApiUtils.error(e.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
