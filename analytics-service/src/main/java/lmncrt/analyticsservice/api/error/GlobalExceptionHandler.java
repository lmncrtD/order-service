package lmncrt.analyticsservice.api.error;

import lmncrt.analyticsservice.exceptions.AnalyticsException;
import lombok.extern.slf4j.Slf4j;
import lmncrt.analyticsservice.dto.errors.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AnalyticsException.class)
    public ResponseEntity<ApiError> handleAnalyticsException(
            AnalyticsException exception
    ) {
        return ResponseEntity.status(
                exception.getStatus()
        ).body(
                new ApiError(
                        exception.getCode(),
                        exception.getMessage()
                )
        );
    }
}