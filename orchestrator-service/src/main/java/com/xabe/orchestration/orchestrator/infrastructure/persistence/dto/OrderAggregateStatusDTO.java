package com.xabe.orchestration.orchestrator.infrastructure.persistence.dto;

public enum OrderAggregateStatusDTO {
  START_SAGA,
  ORDER_CREATED,
  ORDER_CANCELED,
  PAYMENT_PROCESSED,
  PAYMENT_CANCELED,
  SHIPPING_SENT,
  CANCELED,
  SUCCESS;
}
