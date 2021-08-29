package com.xabe.orchestration.shipping.domain.event;

import com.xabe.orchestation.common.infrastructure.Event;
import java.time.Instant;
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
public class ShippingCancelCommandEvent implements Event {

  Long shippingId;

  String purchaseId;

  String userId;

  String productId;

  Instant sentAt;

  String status;

}
