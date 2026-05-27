package br.gov.client.sifap.payment.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentCalculationRequest(
    @NotNull Long beneficiaryId,
    @NotNull @Min(1) @Max(12) Integer competenceMonth,
    @NotNull @DecimalMin("0.00") BigDecimal grossAmount,
    @NotNull @DecimalMin("0.00") BigDecimal nonJudicialDeduction,
    @NotNull @DecimalMin("0.00") BigDecimal judicialDeduction,
    @NotNull @DecimalMin("0.00") BigDecimal correctionAmount
) {
}
