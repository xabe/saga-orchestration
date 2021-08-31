package com.xabe.orchestration.orchestrator.infrastructure.persistence.dto;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class PaymentDTO {

  private Long id;

  private String userId;

  private String productId;

  private Long price;

  @Builder.Default
  private PaymentStatusDTO status = PaymentStatusDTO.ACCEPTED;

  private Instant createdAt;

}