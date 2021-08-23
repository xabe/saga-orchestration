package com.xabe.orchestation.shipping.domain.entity;

import com.xabe.orchestation.common.infrastructure.AggregateRoot;
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
public class Shipping implements AggregateRoot<Long> {

  Long id;

  String purchaseId;

  String userId;

  String productId;

  Long price;

  @Builder.Default
  ShippingStatus status = ShippingStatus.ACCEPTED;

  OffsetDateTime createdAt;
}
