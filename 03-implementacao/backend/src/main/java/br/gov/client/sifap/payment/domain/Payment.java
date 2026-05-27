package br.gov.client.sifap.payment.domain;

import br.gov.client.sifap.beneficiary.domain.Beneficiary;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "beneficiary_id")
  private Beneficiary beneficiary;

  @Column(name = "competence_month", nullable = false)
  private Integer competenceMonth;

  @Column(name = "gross_amount", nullable = false, precision = 14, scale = 2)
  private BigDecimal grossAmount;

  @Column(name = "deduction_amount", nullable = false, precision = 14, scale = 2)
  private BigDecimal deductionAmount;

  @Column(name = "correction_amount", nullable = false, precision = 14, scale = 2)
  private BigDecimal correctionAmount;

  @Column(name = "net_amount", nullable = false, precision = 14, scale = 2)
  private BigDecimal netAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_type", nullable = false, length = 1)
  private PaymentType paymentType;

  @Column(nullable = false)
  private boolean corrected;

  @Column(nullable = false, length = 20)
  private String status;

  public Long getId() {
    return id;
  }

  public Beneficiary getBeneficiary() {
    return beneficiary;
  }

  public void setBeneficiary(Beneficiary beneficiary) {
    this.beneficiary = beneficiary;
  }

  public Integer getCompetenceMonth() {
    return competenceMonth;
  }

  public void setCompetenceMonth(Integer competenceMonth) {
    this.competenceMonth = competenceMonth;
  }

  public BigDecimal getGrossAmount() {
    return grossAmount;
  }

  public void setGrossAmount(BigDecimal grossAmount) {
    this.grossAmount = grossAmount;
  }

  public BigDecimal getDeductionAmount() {
    return deductionAmount;
  }

  public void setDeductionAmount(BigDecimal deductionAmount) {
    this.deductionAmount = deductionAmount;
  }

  public BigDecimal getCorrectionAmount() {
    return correctionAmount;
  }

  public void setCorrectionAmount(BigDecimal correctionAmount) {
    this.correctionAmount = correctionAmount;
  }

  public BigDecimal getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(BigDecimal netAmount) {
    this.netAmount = netAmount;
  }

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public boolean isCorrected() {
    return corrected;
  }

  public void setCorrected(boolean corrected) {
    this.corrected = corrected;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
