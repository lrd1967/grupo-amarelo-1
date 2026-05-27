package br.gov.client.sifap.beneficiary.application;

import br.gov.client.sifap.beneficiary.application.dto.BeneficiaryResponse;
import br.gov.client.sifap.beneficiary.application.dto.BeneficiaryUpsertRequest;
import br.gov.client.sifap.beneficiary.domain.Beneficiary;
import br.gov.client.sifap.beneficiary.domain.BeneficiaryStatus;
import br.gov.client.sifap.beneficiary.domain.ProgramType;
import br.gov.client.sifap.beneficiary.infrastructure.BeneficiaryRepository;
import br.gov.client.sifap.shared.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeneficiaryService {

  private final BeneficiaryRepository beneficiaryRepository;

  public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
    this.beneficiaryRepository = beneficiaryRepository;
  }

  @Transactional
  public BeneficiaryResponse upsert(BeneficiaryUpsertRequest request) {
    validateOperation(request.operationType()); // REQ-CAD-001
    validateCpf(request.cpf()); // REQ-VAL-001

    Beneficiary beneficiary = beneficiaryRepository.findByCpf(request.cpf())
        .orElseGet(Beneficiary::new);

    beneficiary.setCpf(request.cpf());
    beneficiary.setOperationType(request.operationType());
    beneficiary.setBirthDate(request.birthDate());
    beneficiary.setRegionCode(request.regionCode());
    beneficiary.setProgramType(request.programType());
    beneficiary.setFamilyIncome(request.familyIncome());
    beneficiary.setDependents(request.dependents());
    beneficiary.setDocumentsOk(request.documentsOk());
    beneficiary.setNis(request.nis());
    beneficiary.setStatus(resolveInitialStatus(request.birthDate())); // REQ-CAD-002 + REQ-VAL-002

    Beneficiary saved = beneficiaryRepository.save(beneficiary);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public BeneficiaryResponse findById(Long id) {
    Beneficiary beneficiary = beneficiaryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Beneficiary not found"));
    return toResponse(beneficiary);
  }

  @Transactional(readOnly = true)
  public List<BeneficiaryResponse> findAll() {
    return beneficiaryRepository.findAll().stream().map(this::toResponse).toList();
  }

  public boolean evaluateEligibility(Beneficiary beneficiary) {
    // REQ-ELEG-001
    if (beneficiary.getRegionCode() == 99) {
      return true;
    }

    // REQ-ELEG-005 + REQ-VAL-003
    if (hasSpecialPrefix(beneficiary.getCpf())) {
      return true;
    }

    // REQ-ELEG-002
    if (beneficiary.getProgramType() == ProgramType.A) {
      boolean incomeRule = beneficiary.getFamilyIncome().doubleValue() > 600.0 && beneficiary.getDependents() == 0;
      boolean docsRule = !Boolean.TRUE.equals(beneficiary.getDocumentsOk());
      if (incomeRule || docsRule) {
        return false;
      }
    }

    int age = calculateAge(beneficiary.getBirthDate());

    // REQ-ELEG-003
    if (beneficiary.getProgramType() == ProgramType.P && age < 60) {
      return false;
    }

    // REQ-ELEG-004
    if (beneficiary.getProgramType() == ProgramType.T && (age < 16 || age > 65)) {
      return false;
    }

    // REQ-ELEG-005
    return validateSpecialEligibilityFlags(beneficiary);
  }

  private BeneficiaryStatus resolveInitialStatus(LocalDate birthDate) {
    int age = calculateAge(birthDate);
    BeneficiaryStatus status = age > 75 ? BeneficiaryStatus.S : BeneficiaryStatus.A;
    validateStatus(status);
    return status;
  }

  private void validateOperation(String operationType) {
    if (!"I".equals(operationType) && !"A".equals(operationType)) {
      throw new BusinessException("Only operation I or A is allowed");
    }
  }

  private void validateCpf(String cpf) {
    if (cpf.startsWith("000")) {
      return; // REQ-VAL-001 exception
    }
    if (!cpf.matches("\\d{11}")) {
      throw new BusinessException("CPF must have 11 digits");
    }
  }

  private void validateStatus(BeneficiaryStatus status) {
    if (!List.of(BeneficiaryStatus.A, BeneficiaryStatus.S, BeneficiaryStatus.C, BeneficiaryStatus.I, BeneficiaryStatus.D).contains(status)) {
      throw new BusinessException("Invalid beneficiary status");
    }
  }

  private int calculateAge(LocalDate birthDate) {
    return Period.between(birthDate, LocalDate.now()).getYears();
  }

  private boolean hasSpecialPrefix(String cpf) {
    return cpf != null && (cpf.startsWith("999") || cpf.startsWith("000"));
  }

  private boolean validateSpecialEligibilityFlags(Beneficiary beneficiary) {
    String nis = beneficiary.getNis();
    if (beneficiary.getProgramType() == ProgramType.A) {
      boolean needNis = nis == null || nis.isBlank();
      boolean needDependents = beneficiary.getDependents() == null || beneficiary.getDependents() < 1;
      if (needNis || needDependents) {
        return false;
      }
    }
    return true;
  }

  private BeneficiaryResponse toResponse(Beneficiary beneficiary) {
    return new BeneficiaryResponse(
        beneficiary.getId(),
        beneficiary.getCpf(),
        beneficiary.getStatus(),
        beneficiary.getBirthDate(),
        beneficiary.getRegionCode(),
        beneficiary.getProgramType(),
        beneficiary.getFamilyIncome(),
        beneficiary.getDependents(),
        beneficiary.getDocumentsOk(),
        beneficiary.getNis(),
        evaluateEligibility(beneficiary)
    );
  }
}
