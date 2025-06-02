package org.java.fraktl.exceptions.exceptionController;

import static org.java.fraktl.model.response.ResponseStatus.FAILURE;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.java.fraktl.exceptions.errorModel.ApiError;
import org.java.fraktl.exceptions.errorModel.ApiSubError;
import org.java.fraktl.model.response.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        logger.error("MethodArgumentNotValidException: "+ ex.getMessage());

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
            .debugMessage("Validation failed for one or more arguments. Please review the documentation for more details.")
            .subErrors(subErrors)
            .build();

        ApiResponse apiResponse = new ApiResponse(FAILURE, apiError);

        return buildResponseEntity(apiResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiResponse apiResponse, HttpStatus status) {
        return new ResponseEntity<>(apiResponse, status);
    }
}