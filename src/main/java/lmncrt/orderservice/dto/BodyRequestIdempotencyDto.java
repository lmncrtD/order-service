package lmncrt.orderservice.dto;

import lmncrt.orderservice.service.OrderOperations;

import java.util.List;

public record BodyRequestIdempotencyDto<Body>(
        OrderOperations operation,
        Long orderId,
        Body body
) {

}
