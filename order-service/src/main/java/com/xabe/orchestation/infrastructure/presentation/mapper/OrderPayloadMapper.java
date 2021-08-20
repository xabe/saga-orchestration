package com.xabe.orchestation.infrastructure.presentation.mapper;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.infrastructure.presentation.payload.OrderPayload;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface OrderPayloadMapper {

  List<OrderPayload> toOrderPayloads(final List<Order> orders);

  OrderPayload toOrderPayload(Order order);

  Order toOrderEntity(OrderPayload orderPayload);

}
