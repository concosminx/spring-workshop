package com.nimsoc.workshop.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

    log.error("Exception handler code", ex);

    ErrorResponse errorResponse = new ErrorResponse(
        ex.getMessage(),
        request.getDescription(false),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @AllArgsConstructor
  @Getter
  @Setter
  public class ErrorResponse {
    private String message;
    private String details;
    private int status;
    private LocalDateTime timestamp;
  }

}

