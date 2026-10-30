package lmncrt.orderservice.exceptions;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long id) {
        super("Товар с id = " + id + " не найден");
    }

    public ItemNotFoundException() {
        super("Список товаров пуст");
    }

}
