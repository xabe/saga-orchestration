package com.xabe.orchestration.orchestrator.domain.entity;

public enum OrderAggregateStatus {
  START_SAGA,
  ORDER_CREATED,
  PAYMENT_PROCESSED,
  SHIPPING_SENT,
  END_SAGA
}
