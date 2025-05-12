package hu.morkalla.instantpayment.rest.domain;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestDto(
        @Schema(description = "Source account number", example = "12345678-12345678")
        String sourceAccountNumber,
        @Schema(description = "Target account number", example = "12345678-12345678")
        String targetAccountNumber,
        @Schema(description = "Transaction date")
        LocalDateTime transactionDate,
        @Schema(description = "Transaction amount")
        BigDecimal transactionAmount
) {
}
