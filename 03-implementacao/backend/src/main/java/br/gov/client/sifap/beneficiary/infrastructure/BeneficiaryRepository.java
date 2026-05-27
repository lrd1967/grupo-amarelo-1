package br.gov.client.sifap.beneficiary.infrastructure;

import br.gov.client.sifap.beneficiary.domain.Beneficiary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
  Optional<Beneficiary> findByCpf(String cpf);
}
