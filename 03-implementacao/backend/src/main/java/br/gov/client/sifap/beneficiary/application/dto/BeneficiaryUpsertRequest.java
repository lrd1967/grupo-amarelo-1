package br.gov.client.sifap.beneficiary.application.dto;

import br.gov.client.sifap.beneficiary.domain.ProgramType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BeneficiaryUpsertRequest(
    @NotBlank @Pattern(regexp = "\\d{11}") String cpf,
    @NotBlank @Pattern(regexp = "[IA]") String operationType,
    @NotNull LocalDate birthDate,
    @NotNull @Min(1) @Max(99) Integer regionCode,
    @NotNull ProgramType programType,
    @NotNull @Min(0) BigDecimal familyIncome,
    @NotNull @Min(0) Integer dependents,
    @NotNull Boolean documentsOk,
    String nis
) {
}
