package com.xabe.orchestration.orchestrator.infrastructure.application;

import com.xabe.orchestation.common.infrastructure.dispatch.CommandDispatcher;
import com.xabe.orchestration.orchestrator.domain.command.order.OrderCreateCommand;
import com.xabe.orchestration.orchestrator.domain.command.order.OrderCreateCommandContext;
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

  private final CommandDispatcher<OrderCreateCommandContext, OrderAggregate, String> commandDispatcher;

  @Override
  public Uni<List<OrderAggregate>> getOrders() {
    return this.orderRepository.getAll();
  }

  @Override
  public Uni<OrderAggregate> getOrder(final String id) {
    return this.orderRepository.load(id);
  }

  @Override
  public Uni<OrderAggregate> create(final OrderAggregate orderAggregate) {
    return this.orderRepository.save(orderAggregate).invoke(this::sendOrderCreateCommand);
  }

  private void sendOrderCreateCommand(final OrderAggregate orderAggregate) {
    this.commandDispatcher.dispatch(new OrderCreateCommand(orderAggregate.getId(), orderAggregate));
  }

}
