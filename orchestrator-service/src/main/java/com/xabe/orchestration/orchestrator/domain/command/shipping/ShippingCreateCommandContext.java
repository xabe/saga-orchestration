package com.xabe.orchestration.orchestrator.domain.command.shipping;

import com.xabe.orchestation.common.infrastructure.dispatch.CommandContext;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestation.common.infrastructure.repository.Repository;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;

public class ShippingCreateCommandContext extends CommandContext<OrderAggregate, String> {

  public ShippingCreateCommandContext(
      final Repository<OrderAggregate, String> repository,
      final EventPublisher eventPublisher) {
    super(repository, eventPublisher);
  }
}
