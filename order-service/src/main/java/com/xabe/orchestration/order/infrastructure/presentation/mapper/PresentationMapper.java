package com.xabe.orchestration.order.infrastructure.presentation.mapper;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.infrastructure.presentation.payload.OrderPayload;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface PresentationMapper {

  List<OrderPayload> toPayloads(final List<Order> orders);

  OrderPayload toPayload(Order order);

  Order toEntity(OrderPayload orderPayload);

}
