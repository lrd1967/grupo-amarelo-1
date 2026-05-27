package br.gov.client.sifap.beneficiary.application.dto;

import br.gov.client.sifap.beneficiary.domain.BeneficiaryStatus;
import br.gov.client.sifap.beneficiary.domain.ProgramType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BeneficiaryResponse(
    Long id,
    String cpf,
    BeneficiaryStatus status,
    LocalDate birthDate,
    Integer regionCode,
    ProgramType programType,
    BigDecimal familyIncome,
    Integer dependents,
    Boolean documentsOk,
    String nis,
    Boolean eligible
) {
}
