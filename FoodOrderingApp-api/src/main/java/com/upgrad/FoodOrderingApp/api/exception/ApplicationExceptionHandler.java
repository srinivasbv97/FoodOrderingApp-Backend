package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> handleSignUpRestrictedException(
            SignUpRestrictedException sre, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ErrorResponse().code(sre.getCode()).message(sre.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
