package br.gov.client.sifap.beneficiary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 11)
  private String cpf;

  @Column(name = "operation_type", nullable = false, length = 1)
  private String operationType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 1)
  private BeneficiaryStatus status;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "region_code", nullable = false)
  private Integer regionCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "program_type", nullable = false, length = 1)
  private ProgramType programType;

  @Column(name = "family_income", nullable = false, precision = 14, scale = 2)
  private BigDecimal familyIncome;

  @Column(nullable = false)
  private Integer dependents;

  @Column(name = "documents_ok", nullable = false)
  private Boolean documentsOk;

  @Column(length = 16)
  private String nis;

  public Long getId() {
    return id;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getOperationType() {
    return operationType;
  }

  public void setOperationType(String operationType) {
    this.operationType = operationType;
  }

  public BeneficiaryStatus getStatus() {
    return status;
  }

  public void setStatus(BeneficiaryStatus status) {
    this.status = status;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public Integer getRegionCode() {
    return regionCode;
  }

  public void setRegionCode(Integer regionCode) {
    this.regionCode = regionCode;
  }

  public ProgramType getProgramType() {
    return programType;
  }

  public void setProgramType(ProgramType programType) {
    this.programType = programType;
  }

  public BigDecimal getFamilyIncome() {
    return familyIncome;
  }

  public void setFamilyIncome(BigDecimal familyIncome) {
    this.familyIncome = familyIncome;
  }

  public Integer getDependents() {
    return dependents;
  }

  public void setDependents(Integer dependents) {
    this.dependents = dependents;
  }

  public Boolean getDocumentsOk() {
    return documentsOk;
  }

  public void setDocumentsOk(Boolean documentsOk) {
    this.documentsOk = documentsOk;
  }

  public String getNis() {
    return nis;
  }

  public void setNis(String nis) {
    this.nis = nis;
  }
}
