package lmncrt.orderservice.exceptions;

import lombok.Getter;

public class SomeItemsNotFoundException extends RuntimeException{

    public SomeItemsNotFoundException(
    ) {
        super("Часть товаров не найдена");
    }

}
