package com.example.pki.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler({HttpTransferException.class})
    protected ResponseEntity<ErrorMessage> globalExceptionHandler(HttpTransferException ex) {
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), ex.getHttpStatus());
    }
}
