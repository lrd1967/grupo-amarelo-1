package br.gov.client.sifap.payment.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.client.sifap.beneficiary.domain.Beneficiary;
import br.gov.client.sifap.beneficiary.domain.BeneficiaryStatus;
import br.gov.client.sifap.beneficiary.domain.ProgramType;
import br.gov.client.sifap.beneficiary.infrastructure.BeneficiaryRepository;
import br.gov.client.sifap.payment.application.dto.PaymentCalculationRequest;
import br.gov.client.sifap.payment.application.dto.PaymentResponse;
import br.gov.client.sifap.payment.domain.Payment;
import br.gov.client.sifap.payment.domain.PaymentType;
import br.gov.client.sifap.payment.infrastructure.PaymentRepository;
import br.gov.client.sifap.shared.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private BeneficiaryRepository beneficiaryRepository;

  private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    paymentService = new PaymentService(paymentRepository, beneficiaryRepository);
  }

  @Test
  void calculateAndSaveShouldThrowWhenCompetenceMonthIsInvalid() {
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        13,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    assertThrows(BusinessException.class, () -> paymentService.calculateAndSave(request));
  }

  @Test
  void calculateAndSaveShouldThrowWhenBeneficiaryIsNotActive() {
    Beneficiary beneficiary = buildBeneficiary(1L, BeneficiaryStatus.S, ProgramType.A, "11111111111", 70, 10, 1);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        10,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(beneficiary));

    assertThrows(BusinessException.class, () -> paymentService.calculateAndSave(request));
  }

  @Test
  void br001HappyPathShouldAcceptCompetenceWithinRange() {
    Beneficiary beneficiary = buildBeneficiary(1L, BeneficiaryStatus.A, ProgramType.P, "11111111111", 30, 30, 0);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        1,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 1)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PaymentResponse response = paymentService.calculateAndSave(request);

    assertEquals(1, response.competenceMonth());
    assertEquals(PaymentType.N, response.paymentType());
  }

  @Test
  void br003HappyPathShouldApplyRegionalFactorForRegionWithinRange() {
    Beneficiary beneficiary = buildBeneficiary(1L, BeneficiaryStatus.A, ProgramType.P, "11111111111", 30, 10, 0);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        5,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 5)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PaymentResponse response = paymentService.calculateAndSave(request);

    assertEquals(new BigDecimal("1122.00"), response.grossAmount());
  }

  @Test
  void br004HappyPathShouldApplyFamilyFactorForMoreThanFourDependents() {
    Beneficiary beneficiary = buildBeneficiary(1L, BeneficiaryStatus.A, ProgramType.P, "11111111111", 30, 30, 5);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        5,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 5)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PaymentResponse response = paymentService.calculateAndSave(request);

    assertEquals(new BigDecimal("1080.00"), response.grossAmount());
  }

  @Test
  void br005HappyPathShouldApplyAgeFactorForAge65OrMore() {
    Beneficiary beneficiary = buildBeneficiary(1L, BeneficiaryStatus.A, ProgramType.P, "11111111111", 70, 30, 0);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        5,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 5)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PaymentResponse response = paymentService.calculateAndSave(request);

    assertEquals(new BigDecimal("1173.00"), response.grossAmount());
  }

  @Test
  void calculateAndSaveShouldApplyDecemberBonusAndDeductionCap() {
    Beneficiary beneficiary = buildBeneficiary(1L, BeneficiaryStatus.A, ProgramType.A, "11111111111", 70, 10, 1);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        1L,
        12,
        new BigDecimal("1000.00"),
        new BigDecimal("600.00"),
        new BigDecimal("50.00"),
        new BigDecimal("10.00")
    );

    when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 12)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
      Payment payment = invocation.getArgument(0);
      ReflectionTestUtils.setField(payment, "id", 100L);
      return payment;
    });

    PaymentResponse response = paymentService.calculateAndSave(request);

    assertEquals(100L, response.id());
    assertEquals(PaymentType.D, response.paymentType());
    assertEquals(new BigDecimal("1483.85"), response.grossAmount());
    assertEquals(new BigDecimal("495.15"), response.deductionAmount());
    assertEquals(new BigDecimal("998.70"), response.netAmount());
    assertEquals(true, response.corrected());
  }

  @Test
  void br015HappyPathShouldPersistCalculatedPayment() {
    Beneficiary beneficiary = buildBeneficiary(9L, BeneficiaryStatus.A, ProgramType.P, "99999999999", 35, 30, 0);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        9L,
        6,
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(9L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 6)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    paymentService.calculateAndSave(request);

    verify(paymentRepository).save(any(Payment.class));
  }

  @Test
  void calculateAndSaveShouldNotReturnNegativeNetAmount() {
    Beneficiary beneficiary = buildBeneficiary(2L, BeneficiaryStatus.A, ProgramType.P, "22222222222", 30, 30, 0);
    PaymentCalculationRequest request = new PaymentCalculationRequest(
        2L,
        5,
        new BigDecimal("100.00"),
        new BigDecimal("1000.00"),
        new BigDecimal("200.00"),
        BigDecimal.ZERO
    );

    when(beneficiaryRepository.findById(2L)).thenReturn(Optional.of(beneficiary));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, 5)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PaymentResponse response = paymentService.calculateAndSave(request);

    assertEquals(BigDecimal.ZERO, response.netAmount());
  }

  @Test
  void runBatchShouldGeneratePaymentsOnlyForUniqueActiveBeneficiariesWithoutPayment() {
    Beneficiary firstActive = buildBeneficiary(1L, BeneficiaryStatus.A, ProgramType.A, "12345678901", 50, 1, 1);
    Beneficiary duplicateCpf = buildBeneficiary(2L, BeneficiaryStatus.A, ProgramType.A, "12345678901", 40, 1, 1);
    Beneficiary inactive = buildBeneficiary(3L, BeneficiaryStatus.S, ProgramType.A, "33333333333", 50, 1, 1);
    Beneficiary alreadyPaid = buildBeneficiary(4L, BeneficiaryStatus.A, ProgramType.P, "44444444444", 50, 1, 1);

    when(beneficiaryRepository.findAll()).thenReturn(List.of(firstActive, duplicateCpf, inactive, alreadyPaid));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(eq(firstActive), eq(7))).thenReturn(Optional.empty());
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(eq(alreadyPaid), eq(7))).thenReturn(Optional.of(new Payment()));
    when(paymentRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

    List<PaymentResponse> responses = paymentService.runBatch(7);

    assertEquals(1, responses.size());
    assertEquals(1L, responses.getFirst().beneficiaryId());
    assertEquals(PaymentType.N, responses.getFirst().paymentType());
    assertEquals(new BigDecimal("1000.00"), responses.getFirst().netAmount());
  }

  @Test
  void br030HappyPathShouldGenerateForActiveNotPaidBeneficiary() {
    Beneficiary activeNotPaid = buildBeneficiary(6L, BeneficiaryStatus.A, ProgramType.P, "66666666666", 45, 5, 1);

    when(beneficiaryRepository.findAll()).thenReturn(List.of(activeNotPaid));
    when(paymentRepository.findByBeneficiaryAndCompetenceMonth(eq(activeNotPaid), eq(8))).thenReturn(Optional.empty());
    when(paymentRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

    List<PaymentResponse> responses = paymentService.runBatch(8);

    assertEquals(1, responses.size());
    assertEquals(6L, responses.getFirst().beneficiaryId());
  }

  private Beneficiary buildBeneficiary(
      Long id,
      BeneficiaryStatus status,
      ProgramType programType,
      String cpf,
      int age,
      int regionCode,
      int dependents
  ) {
    Beneficiary beneficiary = new Beneficiary();
    ReflectionTestUtils.setField(beneficiary, "id", id);
    beneficiary.setCpf(cpf);
    beneficiary.setOperationType("I");
    beneficiary.setStatus(status);
    beneficiary.setBirthDate(LocalDate.now().minusYears(age));
    beneficiary.setRegionCode(regionCode);
    beneficiary.setProgramType(programType);
    beneficiary.setFamilyIncome(new BigDecimal("500.00"));
    beneficiary.setDependents(dependents);
    beneficiary.setDocumentsOk(true);
    beneficiary.setNis("1234567890123456");
    return beneficiary;
  }
}
