package com.xabe.orchestration.orchestrator.infrastructure.presentation.payload;

public enum OrderAggregateStatusPayload {
  START_SAGA,
  ORDER_CREATED,
  ORDER_CANCELED,
  PAYMENT_PROCESSED,
  PAYMENT_CANCELED,
  SHIPPING_SENT,
  CANCELED,
  SUCCESS;
}
