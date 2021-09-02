package com.xabe.orchestration.orchestrator.infrastructure;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregateStatus;
import com.xabe.orchestration.orchestrator.domain.entity.order.Order;
import com.xabe.orchestration.orchestrator.domain.entity.order.OrderStatus;
import com.xabe.orchestration.orchestrator.domain.entity.payment.Payment;
import com.xabe.orchestration.orchestrator.domain.entity.payment.PaymentStatus;
import com.xabe.orchestration.orchestrator.domain.entity.shipping.Shipping;
import com.xabe.orchestration.orchestrator.domain.entity.shipping.ShippingStatus;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreateCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreatedEvent;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderAggregateDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderAggregateStatusDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderStatusDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.PaymentDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.PaymentStatusDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.ShippingDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.ShippingStatusDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.bson.types.ObjectId;

public class OrderMother {

  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.of(2021, 8, 1, 00, 00, 00, 00, ZoneOffset.UTC);

  public static OrderAggregate createOrderAggregate() {
    return OrderAggregate.builder()
        .id("612cb1ac04e7df1b34068c21")
        .order(createOrder())
        .payment(createPayment())
        .shipping(createShipping())
        .status(OrderAggregateStatus.START_SAGA)
        .createdAt(OFFSET_DATE_TIME)
        .build();
  }

  private static Shipping createShipping() {
    return Shipping.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .price(10L)
        .status(ShippingStatus.ACCEPTED)
        .createdAt(OFFSET_DATE_TIME)
        .build();
  }

  private static Payment createPayment() {
    return Payment.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .price(10L)
        .status(PaymentStatus.ACCEPTED)
        .createdAt(OFFSET_DATE_TIME)
        .build();
  }

  private static Order createOrder() {
    return Order.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .price(10L)
        .status(OrderStatus.CREATED)
        .createdAt(OFFSET_DATE_TIME)
        .build();
  }

  public static OrderAggregateDTO createOrderAggregateDTO() {
    return OrderAggregateDTO.builder()
        .id(new ObjectId("612cb1ac04e7df1b34068c21"))
        .order(createOrderDTO())
        .payment(createPaymentDTO())
        .shipping(createShippingDTO())
        .status(OrderAggregateStatusDTO.START_SAGA)
        .createdAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }

  private static ShippingDTO createShippingDTO() {
    return ShippingDTO.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .price(10L)
        .status(ShippingStatusDTO.ACCEPTED)
        .createdAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }

  private static PaymentDTO createPaymentDTO() {
    return PaymentDTO.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .price(10L)
        .status(PaymentStatusDTO.ACCEPTED)
        .createdAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }

  private static OrderDTO createOrderDTO() {
    return OrderDTO.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .price(10L)
        .status(OrderStatusDTO.CREATED)
        .createdAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }

  public static OrderCreatedEvent createOrderCreatedEvent(final String operationStatus) {
    return OrderCreatedEvent.builder()
        .id(1L)
        .userId("1")
        .productId("2")
        .purchaseId("3")
        .price(10L)
        .operationStatus(operationStatus)
        .status("CREATED")
        .createdAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }

  public static OrderCreateCommandEvent createOrderCreateCommandEvent() {
    return OrderCreateCommandEvent.builder()
        .userId("1")
        .productId("2")
        .price(10L)
        .purchaseId("3")
        .sentAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }

  public static OrderCancelCommandEvent createOrderCancelCommandEvent() {
    return OrderCancelCommandEvent.builder()
        .orderId(5L)
        .userId("1")
        .productId("2")
        .purchaseId("3")
        .sentAt(OFFSET_DATE_TIME.toInstant())
        .build();
  }
}
