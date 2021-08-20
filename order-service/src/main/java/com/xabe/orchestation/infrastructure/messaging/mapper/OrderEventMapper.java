package com.xabe.orchestation.infrastructure.messaging.mapper;

import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestation.domain.event.OrderCreatedEvent;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Random;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface OrderEventMapper {

  Random RANDOM = new Random();

  OrderCreateCommandEvent toAvroCommandEvent(OrderCreateCommand orderCreateCommand);

  @Mapping(source = "sentAt", target = "createdAt")
  @Mapping(target = "price", expression = "java(OrderEventMapper.getPrice())")
  Order toOrderEntity(OrderCreateCommandEvent orderCreateCommandEvent);

  OrderCreatedEvent toOrderEvent(Order order);

  com.xabe.avro.v1.Order toAvroOrderEvent(OrderCreatedEvent orderCreatedEvent);

  default Instant map(final OffsetDateTime value) {
    return Objects.isNull(value) ? Instant.now() : value.toInstant();
  }

  default OffsetDateTime map(final Instant value) {
    return Objects.isNull(value) ? OffsetDateTime.now() : value.atOffset(ZoneOffset.UTC);
  }

  static Long getPrice() {
    return Math.abs(RANDOM.nextLong());
  }

}
