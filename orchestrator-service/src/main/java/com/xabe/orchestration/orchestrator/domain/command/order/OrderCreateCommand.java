package com.xabe.orchestration.orchestrator.domain.command.order;

import com.xabe.orchestation.common.infrastructure.Command;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregateStatus;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreateCommandEvent;
import java.time.Instant;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
@RequiredArgsConstructor
public class OrderCreateCommand implements Command<OrderCreateCommandContext, OrderAggregate, String> {

  Logger logger = LoggerFactory.getLogger(OrderCreateCommand.class);

  String aggregateRootId;

  OrderAggregate orderAggregate;

  @Override
  public void execute(final OrderCreateCommandContext context) {
    context.getRepository().save(this.orderAggregate.toBuilder().status(OrderAggregateStatus.ORDER_CREATED).build())
        .subscribe()
        .with(this.sendOrderCreateCommand(context),
            throwable -> this.logger.error("Error to save orderAggregate with order create command", throwable));
  }

  private Consumer<OrderAggregate> sendOrderCreateCommand(final OrderCreateCommandContext context) {
    return orderAggregate -> {
      final OrderCreateCommandEvent orderCreateCommandEvent = OrderCreateCommandEvent.builder()
          .purchaseId(orderAggregate.getId())
          .price(orderAggregate.getOrder().getPrice())
          .userId(orderAggregate.getOrder().getUserId())
          .productId(orderAggregate.getOrder().getProductId())
          .sentAt(Instant.now())
          .build();
      context.getEventPublisher().tryPublish(orderCreateCommandEvent);
    };
  }
}
