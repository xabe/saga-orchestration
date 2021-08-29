package com.xabe.orchestration.order.infrastructure.messaging.mapper;

import com.xabe.avro.v1.OrderCancelCommand;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.event.OrderCancelCommandEvent;
import com.xabe.orchestration.order.domain.event.OrderCanceledEvent;
import com.xabe.orchestration.order.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestration.order.domain.event.OrderCreatedEvent;
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
public interface MessagingMapper {

  OrderCreateCommandEvent toAvroCreateCommandEvent(OrderCreateCommand orderCreateCommand);

  OrderCancelCommandEvent toAvroCancelCommandEvent(OrderCancelCommand orderCancelCommand);

  @Mapping(source = "sentAt", target = "createdAt")
  @Mapping(target = "status", expression = "java(com.xabe.orchestration.order.domain.entity.OrderStatus.CREATED)")
  Order toEntity(OrderCreateCommandEvent orderCreateCommandEvent);

  @Mapping(source = "orderId", target = "id")
  @Mapping(source = "sentAt", target = "createdAt")
  @Mapping(target = "status", expression = "java(com.xabe.orchestration.order.domain.entity.OrderStatus.CANCELED)")
  Order toEntity(OrderCancelCommandEvent orderCancelCommandEvent);

  OrderCreatedEvent toCreatedEvent(Order order, String operationStatus);

  OrderCanceledEvent toCanceledEvent(Order order, String operationStatus);

  com.xabe.avro.v1.Order toAvroEvent(OrderCreatedEvent orderCreatedEvent);

  com.xabe.avro.v1.Order toAvroEvent(OrderCanceledEvent orderCanceledEvent);

  default Instant map(final OffsetDateTime value) {
    return Objects.isNull(value) ? Instant.now() : value.toInstant();
  }

  default OffsetDateTime map(final Instant value) {
    return Objects.isNull(value) ? OffsetDateTime.now() : value.atOffset(ZoneOffset.UTC);
  }

}
