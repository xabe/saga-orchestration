package com.xabe.orchestration.order.infrastructure;

import static com.xabe.orchestration.order.domain.entity.OrderStatus.CANCELED;
import static com.xabe.orchestration.order.domain.entity.OrderStatus.CREATED;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestration.order.domain.event.OrderCreatedEvent;
import com.xabe.orchestration.order.infrastructure.persistence.dto.OrderDTO;
import com.xabe.orchestration.order.infrastructure.persistence.dto.OrderStatusDTO;
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
        .status(CREATED)
        .createdAt(OffsetDateTime.MAX).build();
  }

  public static Order createOrderNew() {
    return Order.builder()
        .id(1L)
        .purchaseId("222")
        .userId("3")
        .productId("4")
        .price(100L)
        .status(CANCELED)
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
        .price(1L)
        .sentAt(Instant.now())
        .build();
  }
}
