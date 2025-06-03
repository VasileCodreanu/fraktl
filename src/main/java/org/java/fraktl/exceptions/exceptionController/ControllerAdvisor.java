package org.java.fraktl.exceptions.exceptionController;

import static org.java.fraktl.model.response.ResponseStatus.FAILURE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.java.fraktl.exceptions.errorModel.ApiError;
import org.java.fraktl.exceptions.errorModel.ApiSubError;
import org.java.fraktl.exceptions.errorModel.customExceptions.ResourceNotFoundException;
import org.java.fraktl.model.response.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleNoSuchElement(ResourceNotFoundException ex){
      log.error("ResourceNotFoundException: {}", ex.getMessage());

        ApiError apiError = ApiError.builder()
            .status(NOT_FOUND.value())
            .message("The requested resource was not found.")
            .debugMessage(ex.getMessage())
            .subErrors(null)
            .build();

        return buildResponseEntity(apiError, NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleNConstraintViolation(ConstraintViolationException ex){

      log.error("ConstraintViolationException: {}", ex.getMessage());

        ApiError apiError = ApiError.builder()
            .status(BAD_REQUEST.value())
            .message("Request validation failed. Please review the documentation for more details.")
            .debugMessage(ex.getMessage())
            .subErrors(null)
            .build();

        return buildResponseEntity(apiError, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

      log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        List<ApiSubError> subErrors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(err ->
                ApiSubError.builder()
                    .field(err.getField())
                    .rejectedValue(err.getRejectedValue())
                    .message(err.getDefaultMessage())
                    .build())
            .toList();

        ApiError apiError = ApiError.builder()
            .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .message(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
            .debugMessage("Validation failed. Please review the documentation for more details.")
            .subErrors(subErrors)
            .build();

        return buildResponseEntity(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError, HttpStatus status) {

        ApiResponse apiResponse = new ApiResponse(FAILURE, apiError);
        return new ResponseEntity<>(apiResponse, status);
    }
}