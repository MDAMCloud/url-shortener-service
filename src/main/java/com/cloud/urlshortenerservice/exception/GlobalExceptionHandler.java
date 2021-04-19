package com.cloud.urlshortenerservice.exception;

import com.cloud.urlshortenerservice.util.AppResponse;
import com.cloud.urlshortenerservice.util.AppResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public AppResponse<Object> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e){

        return AppResponses.failure(e.getMessage());
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public AppResponse<Object> handleUserAuthenticationException(HttpServletRequest request, Exception e){

        return AppResponses.failure(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AppResponse<Object> handleAnyException(HttpServletRequest request, Exception e){

        return AppResponses.failure("An unknown error has been occurred!");
    }

    /*
        @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e){
        Map<String, String> map = new HashMap<>();
        map.put("ERROR", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<Map<String,String>> handleUserAuthenticationException(HttpServletRequest request, Exception e){
        Map<String, String> map = new HashMap<>();
        map.put("ERROR", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleAnyException(HttpServletRequest request, Exception e){
        Map<String, String> map = new HashMap<>();
        map.put("ERROR", "An unknown error has been occurred!");
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
     */

}
