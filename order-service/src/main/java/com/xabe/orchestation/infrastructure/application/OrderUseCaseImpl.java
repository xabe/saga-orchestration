package com.xabe.orchestation.infrastructure.application;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.domain.repository.OrderRepository;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class OrderUseCaseImpl implements OrderUseCase {

  private final OrderRepository orderRepository;

  @Override
  public Uni<List<Order>> getOrders() {
    return this.orderRepository.getOrders();
  }

  @Override
  public Uni<Order> getOrder(final Long id) {
    return this.orderRepository.getOrder(id);
  }

  @Override
  @ReactiveTransactional
  public Uni<Order> create(final Order order) {
    return this.orderRepository.create(order.toBuilder().price(10L).build());
  }
}
