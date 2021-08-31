package com.xabe.orchestration.orchestrator.infrastructure.application;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.repository.OrderRepository;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class OrderUseCaseImpl implements OrderUseCase {

  private final OrderRepository orderRepository;

  @Override
  public Uni<List<OrderAggregate>> getOrders() {
    return this.orderRepository.getOrders();
  }

  @Override
  public Uni<OrderAggregate> getOrder(final String id) {
    return this.orderRepository.getOrder(id);
  }

  @Override
  public Uni<OrderAggregate> create(final OrderAggregate order) {
    return this.orderRepository.upsert(order);
  }

}
