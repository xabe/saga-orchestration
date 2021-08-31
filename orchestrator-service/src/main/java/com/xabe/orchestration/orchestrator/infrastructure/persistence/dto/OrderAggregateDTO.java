package com.xabe.orchestration.orchestrator.infrastructure.persistence.dto;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
@MongoEntity(collection = "orderAggregate")
public class OrderAggregateDTO {

  @BsonId
  private ObjectId id;

  private OrderDTO order;

  private PaymentDTO payment;

  private ShippingDTO shipping;

  private Instant createdAt;

  private OrderAggregateStatusDTO status;

}