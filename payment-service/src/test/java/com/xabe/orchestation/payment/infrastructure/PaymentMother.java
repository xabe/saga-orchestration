package com.xabe.orchestation.payment.infrastructure;

import com.xabe.orchestation.payment.domain.entity.Payment;
import com.xabe.orchestation.payment.domain.entity.PaymentStatus;
import com.xabe.orchestation.payment.domain.event.PaymentCreateCommandEvent;
import com.xabe.orchestation.payment.domain.event.PaymentCreatedEvent;
import com.xabe.orchestation.payment.infrastructure.persistence.dto.PaymentDTO;
import com.xabe.orchestation.payment.infrastructure.persistence.dto.PaymentStatusDTO;
import java.time.Instant;
import java.time.OffsetDateTime;

public class PaymentMother {

  public static Payment createPayment() {
    return Payment.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status(PaymentStatus.ACCEPTED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static PaymentDTO createPaymentDTO() {
    return PaymentDTO.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status(PaymentStatusDTO.ACCEPTED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static PaymentCreatedEvent createPaymentCreatedEvent() {
    return PaymentCreatedEvent.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status("ACCEPTED")
        .createdAt(Instant.MAX).build();
  }

  public static PaymentCreateCommandEvent createPaymentCreateCommandEvent() {
    return PaymentCreateCommandEvent.builder()
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(1L)
        .sentAt(Instant.now())
        .build();
  }
}
