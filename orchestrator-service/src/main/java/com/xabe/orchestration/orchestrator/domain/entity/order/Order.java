package com.xabe.orchestration.orchestrator.domain.entity.order;

import com.xabe.orchestation.common.infrastructure.Entity;
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
public class Order implements Entity<Long> {

  Long id;

  String userId;

  String productId;

  Long price;

  @Builder.Default
  OrderStatus status = OrderStatus.UNKNOWN;

  OffsetDateTime createdAt;
}
