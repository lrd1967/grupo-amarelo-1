package br.gov.client.sifap.payment.infrastructure;

import br.gov.client.sifap.beneficiary.domain.Beneficiary;
import br.gov.client.sifap.payment.domain.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByBeneficiaryAndCompetenceMonth(Beneficiary beneficiary, Integer competenceMonth);
}
