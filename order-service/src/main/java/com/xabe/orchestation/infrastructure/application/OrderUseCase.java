package com.xabe.orchestation.infrastructure.application;

import com.xabe.orchestation.domain.entity.Order;
import io.smallrye.mutiny.Uni;
import java.util.List;

public interface OrderUseCase {

  Uni<List<Order>> getOrders();

  Uni<Order> getOrder(Long id);

  Uni<Order> create(Order order);

}
