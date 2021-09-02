package com.xabe.orchestration.orchestrator.domain.command.shipping;

import com.xabe.orchestation.common.infrastructure.Command;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregateStatus;
import com.xabe.orchestration.orchestrator.domain.entity.payment.Payment;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCreateCommandEvent;
import io.smallrye.mutiny.Uni;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
@RequiredArgsConstructor
public class ShippingCreateCommand implements Command<ShippingCreateCommandContext, OrderAggregate, String> {

  Logger logger = LoggerFactory.getLogger(ShippingCreateCommand.class);

  String aggregateRootId;

  Payment payment;

  @Override
  public void execute(final ShippingCreateCommandContext context) {
    context.getRepository().load(this.aggregateRootId)
        .flatMap(this.updateOrderAggregate(context))
        .subscribe()
        .with(this.sendShippingCreateCommand(context),
            throwable -> this.logger.error("Error to save orderAggregate with shipping create command", throwable));
  }

  private Function<OrderAggregate, Uni<? extends OrderAggregate>> updateOrderAggregate(final ShippingCreateCommandContext context) {
    return orderAggregate -> {
      orderAggregate.setPayment(this.payment);
      orderAggregate.setStatus(OrderAggregateStatus.SHIPPING_SENT);
      return context.getRepository().save(orderAggregate);
    };
  }

  private Consumer<OrderAggregate> sendShippingCreateCommand(final ShippingCreateCommandContext context) {
    return orderAggregate -> {
      final ShippingCreateCommandEvent shippingCreateCommandEvent = ShippingCreateCommandEvent.builder()
          .purchaseId(orderAggregate.getId())
          .price(orderAggregate.getPayment().getPrice())
          .userId(orderAggregate.getPayment().getUserId())
          .productId(orderAggregate.getPayment().getProductId())
          .sentAt(Instant.now())
          .build();
      context.getEventPublisher().tryPublish(shippingCreateCommandEvent);
    };
  }

}
