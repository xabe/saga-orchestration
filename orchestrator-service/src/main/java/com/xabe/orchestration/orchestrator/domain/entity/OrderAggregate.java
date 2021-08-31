package com.xabe.orchestration.orchestrator.domain.entity;

import com.xabe.orchestation.common.infrastructure.AggregateRoot;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class OrderAggregate implements AggregateRoot<String> {

  private String id;

  private Order order;

  private Payment payment;

  private Shipping shipping;

  private OffsetDateTime createdAt;

  @Builder.Default
  private OrderAggregateStatus status = OrderAggregateStatus.START_SAGA;

}
