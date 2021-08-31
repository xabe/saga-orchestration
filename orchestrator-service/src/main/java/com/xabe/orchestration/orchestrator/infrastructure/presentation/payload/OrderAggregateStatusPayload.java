package com.xabe.orchestration.orchestrator.infrastructure.presentation.payload;

public enum OrderAggregateStatusPayload {
  START_SAGA,
  ORDER_CREATED,
  PAYMENT_PROCESSED,
  SHIPPING_SENT,
  END_SAGA
}
