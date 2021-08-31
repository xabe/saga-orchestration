package com.xabe.orchestration.orchestrator.domain.repository;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderRepository {

  Uni<OrderAggregate> getOrder(String id);

  Uni<List<OrderAggregate>> getOrders();

  Uni<OrderAggregate> upsert(OrderAggregate orderAggregate);
}
