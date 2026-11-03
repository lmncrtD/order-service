package lmncrt.orderservice.api.error;

import lmncrt.orderservice.dto.errors.ApiError;
import lmncrt.orderservice.exceptions.*;
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

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiError> handleItemNotFound(
            ItemNotFoundException exception
    ){
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND
        ).body(
                new ApiError(
                        "ITEM_NOT_FOUND",
                        exception.getMessage()
                )
        );
    }

    @ExceptionHandler(SomeItemsNotFoundException.class)
    public ResponseEntity<ApiError> handleSomeItemsNotFound(
            SomeItemsNotFoundException exception
    ){
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND
        ).body(
                new ApiError(
                        "SOME_ITEMS_NOT_FOUND",
                        exception.getMessage()
                )
        );
    }

    @ExceptionHandler(OrderStatusException.class)
    public ResponseEntity<ApiError> handleOrderStatusException(
            OrderStatusException exception
    ) {
        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST
        ).body(
                new ApiError(
                        "ORDER_STATUS_EXCEPTION",
                        exception.getMessage()
                )
        );
    }

    @ExceptionHandler(OrderAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleAlredyRegisteredOrder(
            OrderAlreadyRegisteredException exception
    ) {
        return ResponseEntity.status(
                HttpStatus.CONFLICT
        ).body(
                new ApiError(
                        "ORDER_ALREADY_REGISTERED",
                        exception.getMessage()
                )
        );
    }

}
