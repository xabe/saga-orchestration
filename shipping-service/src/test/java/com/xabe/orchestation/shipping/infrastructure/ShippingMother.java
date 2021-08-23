package com.xabe.orchestation.shipping.infrastructure;

import com.xabe.orchestation.shipping.domain.entity.Shipping;
import com.xabe.orchestation.shipping.domain.entity.ShippingStatus;
import com.xabe.orchestation.shipping.domain.event.ShippingCreateCommandEvent;
import com.xabe.orchestation.shipping.domain.event.ShippingCreatedEvent;
import com.xabe.orchestation.shipping.infrastructure.persistence.dto.ShippingDTO;
import com.xabe.orchestation.shipping.infrastructure.persistence.dto.ShippingStatusDTO;
import java.time.Instant;
import java.time.OffsetDateTime;

public class ShippingMother {

  public static Shipping createShipping() {
    return Shipping.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status(ShippingStatus.ACCEPTED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static ShippingDTO createShippingDTO() {
    return ShippingDTO.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status(ShippingStatusDTO.ACCEPTED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static ShippingCreatedEvent createShippingCreatedEvent() {
    return ShippingCreatedEvent.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status("ACCEPTED")
        .createdAt(Instant.MAX).build();
  }

  public static ShippingCreateCommandEvent createPaymentCreateCommandEvent() {
    return ShippingCreateCommandEvent.builder()
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(1L)
        .sentAt(Instant.now())
        .build();
  }
}
