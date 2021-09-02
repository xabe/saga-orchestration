package com.xabe.orchestration.orchestrator.domain.command.order;

import com.xabe.orchestation.common.infrastructure.dispatch.CommandContext;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestation.common.infrastructure.repository.Repository;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;

public class OrderCreateCommandContext extends CommandContext<OrderAggregate, String> {

  public OrderCreateCommandContext(
      final Repository<OrderAggregate, String> repository,
      final EventPublisher eventPublisher) {
    super(repository, eventPublisher);
  }
}
