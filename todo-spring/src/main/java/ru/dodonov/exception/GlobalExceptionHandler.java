package ru.dodonov.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleCommonException(Exception exception) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", exception);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFound(Exception exception) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Entity not found", exception);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgument(Exception exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad request", exception);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentials(Exception exception) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Failed to authenticate", exception);
    }

    private ResponseEntity<ErrorMessageResponse> buildErrorResponse(
            HttpStatus status,
            String title,
            Exception exception
    ) {
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                title,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(messageResponse);
    }
}
