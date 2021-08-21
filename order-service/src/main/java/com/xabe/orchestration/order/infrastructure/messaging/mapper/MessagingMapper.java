package com.xabe.orchestration.order.infrastructure.messaging.mapper;

import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestration.order.domain.entity.Order;
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

  OrderCreateCommandEvent toAvroCommandEvent(OrderCreateCommand orderCreateCommand);

  @Mapping(source = "sentAt", target = "createdAt")
  Order toEntity(OrderCreateCommandEvent paymentCreateCommandEvent);

  OrderCreatedEvent toEvent(Order order);

  com.xabe.avro.v1.Order toAvroEvent(OrderCreatedEvent orderCreatedEvent);

  default Instant map(final OffsetDateTime value) {
    return Objects.isNull(value) ? Instant.now() : value.toInstant();
  }

  default OffsetDateTime map(final Instant value) {
    return Objects.isNull(value) ? OffsetDateTime.now() : value.atOffset(ZoneOffset.UTC);
  }

}
