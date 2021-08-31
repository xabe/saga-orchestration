package com.xabe.orchestration.orchestrator.infrastructure.persistence.dto;

public enum OrderAggregateStatusDTO {
  START_SAGA,
  ORDER_CREATED,
  PAYMENT_PROCESSED,
  SHIPPING_SENT,
  END_SAGA
}
