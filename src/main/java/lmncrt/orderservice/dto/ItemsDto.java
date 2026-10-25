package lmncrt.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ItemsDto(

    @NotEmpty(message = "Нужно указать ID товара")
    @Positive
    Long id,

    @Null
    String itemName,

    //Integer itemCount,

    @Null
    BigDecimal itemAmount

) {
}
