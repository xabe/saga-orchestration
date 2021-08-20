package com.xabe.orchestation.domain.entity;

import com.xabe.orchestation.infrastructure.AggregateRoot;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Order implements AggregateRoot<Long> {

  Long id;

  String purchaseId;

  String userId;

  String productId;

  Long price;

  @Builder.Default
  OrderStatus status = OrderStatus.CREATED;

  OffsetDateTime createdAt;
}
