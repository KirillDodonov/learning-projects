package ru.dodonov.exception;

import jakarta.persistence.EntityNotFoundException;
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
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Internal error",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(500)
                .body(messageResponse);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFound(Exception exception) {
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Entity not found",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(404)
                .body(messageResponse);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgument(Exception exception) {
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Bad request",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(400)
                .body(messageResponse);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentials(Exception exception) {
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Failed to authenticate",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(401)
                .body(messageResponse);
    }
}
