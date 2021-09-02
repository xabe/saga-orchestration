package com.xabe.orchestration.orchestrator.domain.command.payment;

import com.xabe.orchestation.common.infrastructure.dispatch.CommandContext;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestation.common.infrastructure.repository.Repository;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;

public class PaymentCreateCommandContext extends CommandContext<OrderAggregate, String> {

  public PaymentCreateCommandContext(
      final Repository<OrderAggregate, String> repository,
      final EventPublisher eventPublisher) {
    super(repository, eventPublisher);
  }
}
