package com.xabe.orchestation.infrastructure;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.domain.entity.OrderStatus;
import com.xabe.orchestation.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestation.domain.event.OrderCreatedEvent;
import com.xabe.orchestation.infrastructure.persistence.dto.OrderDTO;
import com.xabe.orchestation.infrastructure.persistence.dto.OrderStatusDTO;
import java.time.Instant;
import java.time.OffsetDateTime;

public class OrderMother {

  public static Order createOrder() {
    return Order.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status(OrderStatus.CREATED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static OrderDTO createOrderDTO() {
    return OrderDTO.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status(OrderStatusDTO.CREATED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static OrderCreatedEvent createOrderCreatedEvent() {
    return OrderCreatedEvent.builder()
        .id(1L)
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .price(10L)
        .status("CREATED")
        .createdAt(Instant.MAX).build();
  }

  public static OrderCreateCommandEvent createOrderCreateCommandEvent() {
    return OrderCreateCommandEvent.builder()
        .purchaseId("1111")
        .userId("2")
        .productId("3")
        .sentAt(Instant.now())
        .build();
  }
}
