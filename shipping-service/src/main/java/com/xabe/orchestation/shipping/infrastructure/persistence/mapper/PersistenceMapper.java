package com.xabe.orchestation.shipping.infrastructure.persistence.mapper;

import com.xabe.orchestation.shipping.domain.entity.Shipping;
import com.xabe.orchestation.shipping.infrastructure.persistence.dto.ShippingDTO;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface PersistenceMapper {

  Shipping toEntity(ShippingDTO shippingDTO);

  List<Shipping> toEntities(List<ShippingDTO> shippingDTOS);

  ShippingDTO toDTO(Shipping shipping);
}
