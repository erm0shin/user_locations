package org.jitpay.locations.controller;

import lombok.extern.slf4j.Slf4j;
import org.jitpay.locations.exception.AlreadyExistsException;
import org.jitpay.locations.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handle(NotFoundException e) {
        log.error("Resource wasn't found", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Resource wasn't found: " + e.getMessage()));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    protected ResponseEntity<Object> handle(AlreadyExistsException e) {
        log.error("Object can not be saved due to conflicts", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Resource with this id already exists: " + e.getMessage()));
    }

}