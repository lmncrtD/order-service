package lmncrt.orderservice.api.error;

import lmncrt.orderservice.dto.errors.ApiError;
import lmncrt.orderservice.exceptions.ClientNotFoundException;
import lmncrt.orderservice.exceptions.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFound(
            OrderNotFoundException exception
    ){
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND
        ).body(
                new ApiError(
                        "ORDER_NOT_FOUND",
                        exception.getMessage()
                )
        );
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiError> handleClientNotFound(
            ClientNotFoundException exception
    ){
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND
        ).body(
                new ApiError(
                        "CLIENT_NOT_FOUND",
                        exception.getMessage()
                )
        );
    }
}
