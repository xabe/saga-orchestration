package com.xabe.orchestration.order.domain.repository;

import com.xabe.orchestration.order.domain.entity.Order;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderRepository {

  Uni<Order> getOrder(Long id);

  Uni<List<Order>> getOrders();

  Uni<Order> create(Order order);
}
