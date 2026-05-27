package br.gov.client.sifap.payment.infrastructure;

import br.gov.client.sifap.payment.application.PaymentService;
import br.gov.client.sifap.payment.application.dto.PaymentCalculationRequest;
import br.gov.client.sifap.payment.application.dto.PaymentResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping("/calculate")
  @ResponseStatus(HttpStatus.CREATED)
  public PaymentResponse calculate(@Valid @RequestBody PaymentCalculationRequest request) {
    return paymentService.calculateAndSave(request);
  }

  @PostMapping("/batch/{competenceMonth}")
  public List<PaymentResponse> runBatch(@PathVariable Integer competenceMonth) {
    return paymentService.runBatch(competenceMonth);
  }

  @GetMapping
  public List<PaymentResponse> findAll() {
    return paymentService.findAll();
  }
}
