package com.xabe.orchestration.orchestrator.infrastructure.application;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderUseCase {

  Uni<List<OrderAggregate>> getOrders();

  Uni<OrderAggregate> getOrder(String id);

  Uni<OrderAggregate> create(OrderAggregate orderAggregate);
}
