package hu.morkalla.instantpayment.rest.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestDto(

        @Schema(description = "Transaction Id", example = "94d6c0e0-ec58-4fe3-a71b-1cae0c3a23b6")
        @NotBlank
        String transactionId,
        @Schema(description = "Source account number", example = "12345678-12345678")
        @NotBlank
        String sourceAccountNumber,
        @Schema(description = "Target account number", example = "12345678-12345678")
        @NotBlank
        String targetAccountNumber,
        @Schema(description = "Transaction date")
        @PastOrPresent
        LocalDateTime transactionDate,
        @Schema(description = "Transaction amount", example = "3000.3")
        @Min(0)
        BigDecimal transactionAmount
) {
}
