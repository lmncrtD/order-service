package lmncrt.orderservice.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(
            Long id
    ) {
        super("Клиент с id = " + id + " не найден");
    }

}
