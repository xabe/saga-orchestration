package com.xabe.orchestration.orchestrator.infrastructure.persistence.mapper;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderAggregateDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface PersistenceMapper {

  OrderAggregate toEntity(OrderAggregateDTO orderAggregateDTO);

  default String toObjectIdString(final ObjectId id) {
    return id.toString();
  }

  List<OrderAggregate> toOrdersEntity(List<OrderAggregateDTO> orderAggregateDTOS);

  OrderAggregateDTO toDTO(OrderAggregate orderAggregate);

  default ObjectId toObjectId(final String id) {
    return new ObjectId(id);
  }

  default Instant map(final OffsetDateTime value) {
    return Objects.isNull(value) ? Instant.now() : value.toInstant();
  }

  default OffsetDateTime map(final Instant value) {
    return Objects.isNull(value) ? OffsetDateTime.now() : value.atOffset(ZoneOffset.UTC);
  }

}
