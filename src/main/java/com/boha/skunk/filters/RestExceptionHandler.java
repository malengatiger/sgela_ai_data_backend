package com.boha.skunk.filters;

import com.boha.skunk.controllers.OrganizationController;
import com.google.api.client.http.HttpResponseException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Component
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    static final String mm = "\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D\uD83C\uDF0D " +
            "RestExceptionHandler \uD83D\uDD35";
    static final Logger logger = Logger.getLogger(OrganizationController.class.getSimpleName());

    public RestExceptionHandler() {
        logger.info(mm+" .... RestExceptionHandler constructed: ");

    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
    @ExceptionHandler(value
            = { IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        logger.info(mm+"ERROR: handleConflict: " + ex.getMessage());

        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
    // Add other exception handlers for specific exceptions
    // For example:
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        String error = "Resource not found";
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, error, ex));
    }
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, WebRequest request) {
        String error = ex.getMessage();
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }
    @ExceptionHandler(FirebaseAuthException.class)
    protected ResponseEntity<Object> handleFirebaseAuthException(FirebaseAuthException ex, WebRequest request) {
        logger.info(mm+"ERROR: FirebaseAuthException: " + ex.getMessage());
        String error = "Firebase authentication error";
        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, error, ex));
    }
    @ExceptionHandler(HttpResponseException.class)
    protected ResponseEntity<Object> handleHttpResponseException(HttpResponseException ex, WebRequest request) {
        logger.info(mm+"ERROR: HttpResponseException: " + ex.getMessage());
        String error = "Firebase authentication error: HttpResponseException";
        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, error, ex));
    }
    @ExceptionHandler(ExecutionException.class)
    protected ResponseEntity<Object> handleExecutionException(ExecutionException ex, WebRequest request) {
        logger.info(mm+"ERROR: ExecutionException: " + ex.getMessage());
        String error = "Firebase authentication error: HttpResponseException";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }
}