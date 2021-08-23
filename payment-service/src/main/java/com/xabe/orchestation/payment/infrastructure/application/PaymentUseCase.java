package com.xabe.orchestation.payment.infrastructure.application;

import com.xabe.orchestation.payment.domain.entity.Payment;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface PaymentUseCase {

  Uni<List<Payment>> getPayments();

  Uni<Payment> getPayment(Long id);

  Uni<Payment> create(Payment payment);

}
