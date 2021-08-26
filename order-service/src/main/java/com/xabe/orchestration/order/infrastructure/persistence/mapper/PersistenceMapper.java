package com.xabe.orchestration.order.infrastructure.persistence.mapper;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.infrastructure.persistence.dto.OrderDTO;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface PersistenceMapper {

  Order toEntity(OrderDTO orderDTO);

  List<Order> toOrdersEntity(List<OrderDTO> orderDTOS);

  @Mapping(target = "id", ignore = true)
  OrderDTO toDTO(Order order);
}
