package com.xabe.orchestration.orchestrator.infrastructure.presentation.mapper;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderAggregatePayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderRequestPayload;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface PresentationMapper {

  List<OrderAggregatePayload> toPayloads(final List<OrderAggregate> orderAggregates);

  OrderAggregatePayload toPayload(OrderAggregate orderAggregate);

  @Mapping(source = "userId", target = "order.userId")
  @Mapping(source = "productId", target = "order.productId")
  @Mapping(source = "price", target = "order.price")
  @Mapping(source = "userId", target = "payment.userId")
  @Mapping(source = "productId", target = "payment.productId")
  @Mapping(source = "price", target = "payment.price")
  @Mapping(source = "userId", target = "shipping.userId")
  @Mapping(source = "productId", target = "shipping.productId")
  @Mapping(source = "price", target = "shipping.price")
  OrderAggregate toEntity(OrderRequestPayload orderRequestPayload);

}
