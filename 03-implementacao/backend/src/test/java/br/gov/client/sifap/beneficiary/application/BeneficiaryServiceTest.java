package br.gov.client.sifap.beneficiary.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.client.sifap.beneficiary.application.dto.BeneficiaryResponse;
import br.gov.client.sifap.beneficiary.application.dto.BeneficiaryUpsertRequest;
import br.gov.client.sifap.beneficiary.domain.Beneficiary;
import br.gov.client.sifap.beneficiary.domain.BeneficiaryStatus;
import br.gov.client.sifap.beneficiary.domain.ProgramType;
import br.gov.client.sifap.beneficiary.infrastructure.BeneficiaryRepository;
import br.gov.client.sifap.shared.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceTest {

  @Mock
  private BeneficiaryRepository beneficiaryRepository;

  private BeneficiaryService beneficiaryService;

  @BeforeEach
  void setUp() {
    beneficiaryService = new BeneficiaryService(beneficiaryRepository);
  }

  @Test
  void upsertShouldThrowWhenOperationTypeIsInvalid() {
    BeneficiaryUpsertRequest request = buildRequest("12345678901", "X", LocalDate.now().minusYears(30));

    assertThrows(BusinessException.class, () -> beneficiaryService.upsert(request));
    verify(beneficiaryRepository, never()).save(any(Beneficiary.class));
  }

  @Test
  void upsertShouldThrowWhenCpfIsInvalid() {
    BeneficiaryUpsertRequest request = buildRequest("ABC", "I", LocalDate.now().minusYears(30));

    assertThrows(BusinessException.class, () -> beneficiaryService.upsert(request));
    verify(beneficiaryRepository, never()).save(any(Beneficiary.class));
  }

  @Test
  void upsertShouldSetSeniorStatusWhenAgeIsAbove75() {
    LocalDate birthDate = LocalDate.now().minusYears(80);
    BeneficiaryUpsertRequest request = buildRequest("12345678901", "I", birthDate);

    when(beneficiaryRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
    when(beneficiaryRepository.save(any(Beneficiary.class))).thenAnswer(invocation -> {
      Beneficiary b = invocation.getArgument(0);
      ReflectionTestUtils.setField(b, "id", 10L);
      return b;
    });

    BeneficiaryResponse response = beneficiaryService.upsert(request);

    assertEquals(BeneficiaryStatus.S, response.status());
    assertEquals(10L, response.id());
  }

  @Test
  void br024HappyPathShouldAcceptOperationA() {
    BeneficiaryUpsertRequest request = buildRequest("12345678901", "A", LocalDate.now().minusYears(40));

    when(beneficiaryRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
    when(beneficiaryRepository.save(any(Beneficiary.class))).thenAnswer(invocation -> {
      Beneficiary b = invocation.getArgument(0);
      ReflectionTestUtils.setField(b, "id", 11L);
      return b;
    });

    BeneficiaryResponse response = beneficiaryService.upsert(request);

    assertEquals(11L, response.id());
    assertEquals(BeneficiaryStatus.A, response.status());
  }

  @Test
  void br025HappyPathShouldSetInitialStatusAWhenAgeIsAtMost75() {
    BeneficiaryUpsertRequest request = buildRequest("12345678901", "I", LocalDate.now().minusYears(70));

    when(beneficiaryRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
    when(beneficiaryRepository.save(any(Beneficiary.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BeneficiaryResponse response = beneficiaryService.upsert(request);

    assertEquals(BeneficiaryStatus.A, response.status());
  }

  @Test
  void br026HappyPathShouldAcceptCpfWith000PrefixAsException() {
    BeneficiaryUpsertRequest request = buildRequest("000ABC12345", "I", LocalDate.now().minusYears(35));

    when(beneficiaryRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
    when(beneficiaryRepository.save(any(Beneficiary.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BeneficiaryResponse response = beneficiaryService.upsert(request);

    assertEquals("000ABC12345", response.cpf());
  }

  @Test
  void evaluateEligibilityShouldReturnFalseForProgramAWithIncomeRuleViolation() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setProgramType(ProgramType.A);
    beneficiary.setFamilyIncome(new BigDecimal("700.00"));
    beneficiary.setDependents(0);
    beneficiary.setDocumentsOk(true);

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertFalse(eligible);
  }

  @Test
  void evaluateEligibilityShouldReturnTrueForSpecialRegion() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setRegionCode(99);

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertTrue(eligible);
  }

  @Test
  void br020HappyPathProgramAShouldBeEligibleWhenConstraintsAreSatisfied() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setProgramType(ProgramType.A);
    beneficiary.setFamilyIncome(new BigDecimal("550.00"));
    beneficiary.setDependents(2);
    beneficiary.setDocumentsOk(true);
    beneficiary.setNis("1234567890123456");

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertTrue(eligible);
  }

  @Test
  void br021HappyPathProgramPShouldBeEligibleWithAge60OrMore() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setProgramType(ProgramType.P);
    beneficiary.setBirthDate(LocalDate.now().minusYears(60));

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertTrue(eligible);
  }

  @Test
  void br022HappyPathProgramTShouldBeEligibleWithinAgeRange() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setProgramType(ProgramType.T);
    beneficiary.setBirthDate(LocalDate.now().minusYears(35));

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertTrue(eligible);
  }

  @Test
  void br023HappyPathProgramAShouldBeEligibleWhenSpecialFlagsAreSatisfied() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setProgramType(ProgramType.A);
    beneficiary.setNis("1234567890123456");
    beneficiary.setDependents(3);

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertTrue(eligible);
  }

  @Test
  void br028HappyPathShouldBypassValidationForCpfPrefix999() {
    Beneficiary beneficiary = buildBeneficiary();
    beneficiary.setCpf("99912345678");
    beneficiary.setDocumentsOk(false);
    beneficiary.setProgramType(ProgramType.A);
    beneficiary.setFamilyIncome(new BigDecimal("900.00"));
    beneficiary.setDependents(0);

    boolean eligible = beneficiaryService.evaluateEligibility(beneficiary);

    assertTrue(eligible);
  }

  @Test
  void findByIdShouldThrowWhenBeneficiaryDoesNotExist() {
    when(beneficiaryRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> beneficiaryService.findById(999L));
  }

  private BeneficiaryUpsertRequest buildRequest(String cpf, String operationType, LocalDate birthDate) {
    return new BeneficiaryUpsertRequest(
        cpf,
        operationType,
        birthDate,
        10,
        ProgramType.A,
        new BigDecimal("500.00"),
        2,
        true,
        "1234567890123456"
    );
  }

  private Beneficiary buildBeneficiary() {
    Beneficiary beneficiary = new Beneficiary();
    beneficiary.setCpf("12345678901");
    beneficiary.setOperationType("I");
    beneficiary.setStatus(BeneficiaryStatus.A);
    beneficiary.setBirthDate(LocalDate.now().minusYears(30));
    beneficiary.setRegionCode(1);
    beneficiary.setProgramType(ProgramType.A);
    beneficiary.setFamilyIncome(new BigDecimal("500.00"));
    beneficiary.setDependents(2);
    beneficiary.setDocumentsOk(true);
    beneficiary.setNis("1234567890123456");
    return beneficiary;
  }
}
