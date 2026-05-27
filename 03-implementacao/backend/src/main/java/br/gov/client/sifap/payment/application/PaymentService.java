package br.gov.client.sifap.payment.application;

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
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

  private static final BigDecimal THIRTY_PERCENT = new BigDecimal("0.30");
  private static final BigDecimal FIFTEEN_PERCENT = new BigDecimal("0.15");

  private final PaymentRepository paymentRepository;
  private final BeneficiaryRepository beneficiaryRepository;

  public PaymentService(PaymentRepository paymentRepository, BeneficiaryRepository beneficiaryRepository) {
    this.paymentRepository = paymentRepository;
    this.beneficiaryRepository = beneficiaryRepository;
  }

  @Transactional
  public PaymentResponse calculateAndSave(PaymentCalculationRequest request) {
    validateCompetence(request.competenceMonth()); // REQ-PAY-001

    Beneficiary beneficiary = beneficiaryRepository.findById(request.beneficiaryId())
        .orElseThrow(() -> new EntityNotFoundException("Beneficiary not found"));

    if (beneficiary.getStatus() != BeneficiaryStatus.A) {
      throw new BusinessException("Only ACTIVE beneficiaries can have payment calculated"); // REQ-PAY-002
    }

    Payment payment = paymentRepository.findByBeneficiaryAndCompetenceMonth(beneficiary, request.competenceMonth())
        .orElseGet(Payment::new);

    BigDecimal gross = request.grossAmount();
    BigDecimal ageFactor = resolveAgeFactor(beneficiary);
    BigDecimal regionFactor = resolveRegionFactor(beneficiary.getRegionCode());
    BigDecimal familyFactor = resolveFamilyFactor(beneficiary.getDependents());

    BigDecimal adjustedGross = gross.multiply(ageFactor).multiply(regionFactor).multiply(familyFactor)
        .setScale(2, RoundingMode.HALF_UP);

    if (request.competenceMonth() == 12 && beneficiary.getProgramType() == ProgramType.A) {
      adjustedGross = adjustedGross.add(adjustedGross.multiply(FIFTEEN_PERCENT)).setScale(2, RoundingMode.HALF_UP); // REQ-PAY-007
    }

    PaymentType type = request.competenceMonth() == 12 ? PaymentType.D : PaymentType.N; // REQ-PAY-006

    BigDecimal deduction = calculateDeduction(adjustedGross, request.nonJudicialDeduction(), request.judicialDeduction()); // REQ-DSCT
    BigDecimal correction = request.correctionAmount();
    BigDecimal net = adjustedGross.subtract(deduction).add(correction);

    if (net.signum() < 0) {
      net = BigDecimal.ZERO; // REQ-DSCT-002
    }

    payment.setBeneficiary(beneficiary);
    payment.setCompetenceMonth(request.competenceMonth());
    payment.setGrossAmount(adjustedGross);
    payment.setDeductionAmount(deduction);
    payment.setCorrectionAmount(correction);
    payment.setNetAmount(net);
    payment.setPaymentType(type);
    payment.setCorrected(correction.signum() > 0);
    payment.setStatus("PENDING");

    Payment saved = paymentRepository.save(payment);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<PaymentResponse> findAll() {
    return paymentRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional
  public List<PaymentResponse> runBatch(Integer competenceMonth) {
    validateCompetence(competenceMonth);

    Set<String> seenCpf = new HashSet<>(); // REQ-BATCH-001
    List<Beneficiary> beneficiaries = beneficiaryRepository.findAll();

    List<Payment> generated = beneficiaries.stream()
        .filter(b -> seenCpf.add(b.getCpf()))
        .filter(b -> b.getStatus() == BeneficiaryStatus.A)
        .filter(b -> paymentRepository.findByBeneficiaryAndCompetenceMonth(b, competenceMonth).isEmpty())
        .map(b -> {
          Payment p = new Payment();
          p.setBeneficiary(b);
          p.setCompetenceMonth(competenceMonth);
          p.setGrossAmount(new BigDecimal("1000.00"));
          p.setDeductionAmount(BigDecimal.ZERO);
          p.setCorrectionAmount(BigDecimal.ZERO);
          p.setNetAmount(new BigDecimal("1000.00"));
          p.setPaymentType(competenceMonth == 12 ? PaymentType.D : PaymentType.N);
          p.setCorrected(false);
          p.setStatus("PENDING");
          return p;
        })
        .toList();

    return paymentRepository.saveAll(generated).stream().map(this::toResponse).toList();
  }

  private void validateCompetence(Integer competenceMonth) {
    if (competenceMonth == null || competenceMonth < 1 || competenceMonth > 12) {
      throw new BusinessException("Competence month must be between 1 and 12");
    }
  }

  private BigDecimal resolveRegionFactor(Integer regionCode) {
    return regionCode >= 1 && regionCode <= 25 ? new BigDecimal("1.10") : BigDecimal.ONE;
  }

  private BigDecimal resolveFamilyFactor(Integer dependents) {
    if (dependents <= 2) {
      return new BigDecimal("1.02");
    }
    if (dependents <= 4) {
      return new BigDecimal("1.05");
    }
    return new BigDecimal("1.08");
  }

  private BigDecimal resolveAgeFactor(Beneficiary beneficiary) {
    int age = java.time.Period.between(beneficiary.getBirthDate(), java.time.LocalDate.now()).getYears();
    if (age >= 65) {
      return new BigDecimal("1.15");
    }
    if (age >= 60) {
      return new BigDecimal("1.10");
    }
    if (age < 18) {
      return new BigDecimal("1.05");
    }
    return BigDecimal.ONE;
  }

  private BigDecimal calculateDeduction(BigDecimal gross, BigDecimal nonJudicial, BigDecimal judicial) {
    BigDecimal maxNonJudicial = gross.multiply(THIRTY_PERCENT).setScale(2, RoundingMode.DOWN);
    BigDecimal cappedNonJudicial = nonJudicial.min(maxNonJudicial).setScale(2, RoundingMode.DOWN);
    return cappedNonJudicial.add(judicial).setScale(2, RoundingMode.DOWN);
  }

  private PaymentResponse toResponse(Payment payment) {
    return new PaymentResponse(
        payment.getId(),
        payment.getBeneficiary().getId(),
        payment.getCompetenceMonth(),
        payment.getGrossAmount(),
        payment.getDeductionAmount(),
        payment.getCorrectionAmount(),
        payment.getNetAmount(),
        payment.getPaymentType(),
        payment.getStatus(),
        payment.isCorrected()
    );
  }
}
