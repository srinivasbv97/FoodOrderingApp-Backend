package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.common.UnexpectedException;
import com.upgrad.FoodOrderingApp.service.exception.*;
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

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(
            AuthenticationFailedException afe, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ErrorResponse().code(afe.getCode()).message(afe.getErrorMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationFailedException(
            AuthorizationFailedException afe, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ErrorResponse().code(afe.getCode()).message(afe.getErrorMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> handleUpdateCustomerException(
            UpdateCustomerException uce, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ErrorResponse().code(uce.getCode()).message(uce.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            UnexpectedException ue, WebRequest webRequest) {
        return new ResponseEntity<>(
                new ErrorResponse().code(ue.getErrorCode().getCode()).message(ue.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> saveAddressException(
            SaveAddressException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(
            AddressNotFoundException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()),
                HttpStatus.NOT_FOUND);
    }
}
