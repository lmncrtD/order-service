package lmncrt.orderservice.dto.errors;

public record ApiError(
        String code,
        String message
) {
}
