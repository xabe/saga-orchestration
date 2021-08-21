package com.xabe.orchestration.order.infrastructure.application;

import com.xabe.orchestration.order.domain.entity.Order;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderUseCase {

  Uni<List<Order>> getOrders();

  Uni<Order> getOrder(Long id);

  Uni<Order> create(Order order);

}
