package com.andre_nathan.gym_webservice.schedule.web;

import com.andre_nathan.gym_webservice.schedule.application.exception.ClassSessionNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.exception.RoomBookingConflictException;
import com.andre_nathan.gym_webservice.schedule.application.exception.ScheduleNotFoundException;
import com.andre_nathan.gym_webservice.schedule.application.exception.TrainerScheduleConflictException;
import com.andre_nathan.gym_webservice.schedule.domain.exception.InvalidCapacityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            ScheduleNotFoundException.class,
            ClassSessionNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler({
            RoomBookingConflictException.class,
            TrainerScheduleConflictException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflict(RuntimeException ex, HttpServletRequest req) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler({
            InvalidCapacityException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> details = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : error.getDefaultMessage())
                .toList();

        return buildError(HttpStatus.BAD_REQUEST, "Validation failed.", req, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return buildError(HttpStatus.BAD_REQUEST, "Request body is invalid or malformed.", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() == null ? "Unexpected error" : ex.getMessage(),
                req,
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest req,
            List<String> details
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
