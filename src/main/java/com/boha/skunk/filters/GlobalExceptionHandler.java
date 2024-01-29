package com.boha.skunk.filters;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "GlobalExceptionHandler \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getSimpleName());
    public GlobalExceptionHandler() {
        logger.info(mm+" .... GlobalExceptionHandler constructed: ");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        // Handle the exception and return an appropriate response
        logger.info(mm+"An error is being intercepted!!! " + ex.getMessage());
        //ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }

    // Add more exception handler methods for specific exceptions if needed
    // For example:
    // @ExceptionHandler(NullPointerException.class)
    // public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
    //     // Handle the specific exception and return an appropriate response
    // }
}
