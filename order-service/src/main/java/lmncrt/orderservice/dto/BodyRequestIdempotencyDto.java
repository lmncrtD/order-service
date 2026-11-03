package lmncrt.orderservice.dto;

import java.util.UUID;

import lmncrt.orderservice.service.OrderOperations;

import java.util.List;

public record BodyRequestIdempotencyDto<Body>(
        OrderOperations operation,
        UUID orderId,
        Body body
) {

}
