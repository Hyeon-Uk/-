package com.hyeonuk.chatting.integ.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiUtils{
    public static <T> ResponseEntity<ApiResult<T>> success(T response, HttpStatus status){
        return new ResponseEntity(new ApiResult<>(true,response,null),status);
    }

    public static ApiResult<?> error(Throwable throwable,HttpStatus status){
        return new ApiResult<>(false,null,new ApiError(throwable,status));
    }

    public static ApiResult<?> error(String message,HttpStatus status){
        return new ApiResult<>(false,null,new ApiError(message,status));
    }

    @Getter
    public static class ApiError{
        private final String message;
        private final int status;
        ApiError(Throwable throwable,HttpStatus status){
            this(throwable.getMessage(),status);
        }

        ApiError(String message, HttpStatus status) {
            this.message = message;
            this.status = status.value();
        }
    }

    @Getter
    public static class ApiResult<T>{
        private final boolean success;
        private final T response;
        private final ApiError error;

        private ApiResult(boolean success,T response,ApiError error){
            this.success=success;
            this.response=response;
            this.error=error;
        }
    }

}
