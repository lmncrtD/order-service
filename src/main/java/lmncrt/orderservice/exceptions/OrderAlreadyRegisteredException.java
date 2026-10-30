package lmncrt.orderservice.exceptions;

import java.util.UUID;

public class OrderAlreadyRegisteredException extends RuntimeException{

    public OrderAlreadyRegisteredException(UUID idempotencyKey){
        super("Ключ идемпотентности = " + idempotencyKey + " уже использован для другого запроса");
    }

}
