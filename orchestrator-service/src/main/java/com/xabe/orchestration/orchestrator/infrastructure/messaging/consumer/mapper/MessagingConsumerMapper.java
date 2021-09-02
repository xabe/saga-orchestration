package com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.mapper;

import com.xabe.orchestration.orchestrator.domain.entity.order.Order;
import com.xabe.orchestration.orchestrator.domain.entity.payment.Payment;
import com.xabe.orchestration.orchestrator.domain.entity.shipping.Shipping;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCanceledEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreatedEvent;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCanceledEvent;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCreatedEvent;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCanceledEvent;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCreatedEvent;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface MessagingConsumerMapper {

  @Mapping(source = "order.id", target = "id")
  @Mapping(source = "order.purchaseId", target = "purchaseId")
  @Mapping(source = "order.userId", target = "userId")
  @Mapping(source = "order.productId", target = "productId")
  @Mapping(source = "order.price", target = "price")
  @Mapping(source = "order.status", target = "status")
  @Mapping(source = "order.createdAt", target = "createdAt")
  OrderCreatedEvent toAvroOrderCreatedEvent(com.xabe.avro.v1.OrderCreatedEvent orderCreatedEvent);

  @Mapping(source = "payment.id", target = "id")
  @Mapping(source = "payment.purchaseId", target = "purchaseId")
  @Mapping(source = "payment.userId", target = "userId")
  @Mapping(source = "payment.productId", target = "productId")
  @Mapping(source = "payment.price", target = "price")
  @Mapping(source = "payment.status", target = "status")
  @Mapping(source = "payment.createdAt", target = "createdAt")
  PaymentCreatedEvent toAvroPaymentCreatedEvent(com.xabe.avro.v1.PaymentCreatedEvent paymentCreatedEvent);

  @Mapping(source = "shipping.id", target = "id")
  @Mapping(source = "shipping.purchaseId", target = "purchaseId")
  @Mapping(source = "shipping.userId", target = "userId")
  @Mapping(source = "shipping.productId", target = "productId")
  @Mapping(source = "shipping.price", target = "price")
  @Mapping(source = "shipping.status", target = "status")
  @Mapping(source = "shipping.createdAt", target = "createdAt")
  ShippingCreatedEvent toAvroShippingCreatedEvent(com.xabe.avro.v1.ShippingCreatedEvent shippingCreatedEvent);

  @Mapping(source = "order.id", target = "id")
  @Mapping(source = "order.purchaseId", target = "purchaseId")
  @Mapping(source = "order.userId", target = "userId")
  @Mapping(source = "order.productId", target = "productId")
  @Mapping(source = "order.price", target = "price")
  @Mapping(source = "order.status", target = "status")
  @Mapping(source = "order.createdAt", target = "createdAt")
  OrderCanceledEvent toAvroOrderCanceledEvent(com.xabe.avro.v1.OrderCanceledEvent orderCanceledEvent);

  @Mapping(source = "payment.id", target = "id")
  @Mapping(source = "payment.purchaseId", target = "purchaseId")
  @Mapping(source = "payment.userId", target = "userId")
  @Mapping(source = "payment.productId", target = "productId")
  @Mapping(source = "payment.price", target = "price")
  @Mapping(source = "payment.status", target = "status")
  @Mapping(source = "payment.createdAt", target = "createdAt")
  PaymentCanceledEvent toAvroPaymentCanceledEvent(com.xabe.avro.v1.PaymentCanceledEvent paymentCanceledEvent);

  @Mapping(source = "shipping.id", target = "id")
  @Mapping(source = "shipping.purchaseId", target = "purchaseId")
  @Mapping(source = "shipping.userId", target = "userId")
  @Mapping(source = "shipping.productId", target = "productId")
  @Mapping(source = "shipping.price", target = "price")
  @Mapping(source = "shipping.status", target = "status")
  @Mapping(source = "shipping.createdAt", target = "createdAt")
  ShippingCreatedEvent toAvroShippingCanceledEvent(com.xabe.avro.v1.ShippingCanceledEvent shippingCanceledEvent);

  Order toOrderEntity(OrderCreatedEvent orderCreatedEvent);

  Order toOrderEntity(OrderCanceledEvent orderCanceledEvent);

  Payment toPaymentEntity(PaymentCreatedEvent paymentCreatedEvent);

  Payment toPaymentEntity(PaymentCanceledEvent paymentCanceledEvent);

  Shipping toShippingEntity(ShippingCreatedEvent shippingCreatedEvent);

  Shipping toShippingEntity(ShippingCanceledEvent shippingCanceledEvent);

  default OffsetDateTime map(final Instant value) {
    return Objects.isNull(value) ? OffsetDateTime.now() : value.atOffset(ZoneOffset.UTC);
  }
}
