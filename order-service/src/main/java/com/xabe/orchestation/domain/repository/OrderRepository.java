package com.xabe.orchestation.domain.repository;

import com.xabe.orchestation.domain.entity.Order;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderRepository {

  Uni<Order> getOrder(Long id);

  Uni<List<Order>> getOrders();

  Uni<Order> create(Order order);
}
