package lmncrt.analyticsservice.dto.errors;

public record ApiError(
        String code,
        String message
) {
}
