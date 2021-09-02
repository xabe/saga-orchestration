package com.xabe.orchestration.orchestrator.infrastructure.messaging.publisher.mapper;

import com.xabe.avro.v1.OrderCancelCommand;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.avro.v1.PaymentCancelCommand;
import com.xabe.avro.v1.PaymentCreateCommand;
import com.xabe.avro.v1.ShippingCancelCommand;
import com.xabe.avro.v1.ShippingCreateCommand;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreateCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCreateCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCreateCommandEvent;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface MessagingPublisherMapper {

  OrderCreateCommand toAvroCommandEvent(OrderCreateCommandEvent orderCreateCommandEvent);

  OrderCancelCommand toAvroCommandEvent(OrderCancelCommandEvent orderCancelCommandEvent);

  PaymentCreateCommand toAvroCommandEvent(PaymentCreateCommandEvent paymentCreateCommandEvent);

  PaymentCancelCommand toAvroCommandEvent(PaymentCancelCommandEvent paymentCancelCommandEvent);

  ShippingCreateCommand toAvroCommandEvent(ShippingCreateCommandEvent shippingCreateCommandEvent);

  ShippingCancelCommand toAvroCommandEvent(ShippingCancelCommandEvent shippingCancelCommandEvent);

  default Instant map(final OffsetDateTime value) {
    return Objects.isNull(value) ? Instant.now() : value.toInstant();
  }
}
