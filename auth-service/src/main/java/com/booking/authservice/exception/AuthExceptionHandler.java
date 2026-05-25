package com.booking.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  ProblemDetail handleStatus(ResponseStatusException ex) {
    return ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .orElse("Validation failed");
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, msg);
  }
}
