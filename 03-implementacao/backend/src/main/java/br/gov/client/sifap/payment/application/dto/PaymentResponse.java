package br.gov.client.sifap.payment.application.dto;

import br.gov.client.sifap.payment.domain.PaymentType;
import java.math.BigDecimal;

public record PaymentResponse(
    Long id,
    Long beneficiaryId,
    Integer competenceMonth,
    BigDecimal grossAmount,
    BigDecimal deductionAmount,
    BigDecimal correctionAmount,
    BigDecimal netAmount,
    PaymentType paymentType,
    String status,
    boolean corrected
) {
}
