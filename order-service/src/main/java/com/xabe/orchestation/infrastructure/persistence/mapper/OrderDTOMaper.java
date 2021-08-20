package com.xabe.orchestation.infrastructure.persistence.mapper;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.infrastructure.persistence.dto.OrderDTO;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface OrderDTOMaper {

  Order toOrderEntity(OrderDTO orderDTO);

  List<Order> toOrdersEntity(List<OrderDTO> orderDTOS);

  OrderDTO toOrderDTO(Order order);
}
