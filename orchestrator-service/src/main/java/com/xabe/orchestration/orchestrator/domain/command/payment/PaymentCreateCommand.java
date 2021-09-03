package com.xabe.orchestration.orchestrator.domain.command.payment;

import com.xabe.orchestation.common.infrastructure.Command;
import com.xabe.orchestation.common.infrastructure.exception.EntityNotFoundException;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregateStatus;
import com.xabe.orchestration.orchestrator.domain.entity.order.Order;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCreateCommandEvent;
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
public class PaymentCreateCommand implements Command<PaymentCreateCommandContext, OrderAggregate, String> {

  Logger logger = LoggerFactory.getLogger(PaymentCreateCommand.class);

  String aggregateRootId;

  Order order;

  @Override
  public void execute(final PaymentCreateCommandContext context) {
    context.getRepository().load(this.aggregateRootId)
        .onItem().ifNull().failWith(() -> new EntityNotFoundException("OrderAggregate"))
        .flatMap(this.updateOrderAggregate(context))
        .subscribe()
        .with(this.sendPaymentCreateCommand(context),
            throwable -> this.logger.error("Error to save orderAggregate {} with payment create command", this.aggregateRootId, throwable));
  }

  private Function<OrderAggregate, Uni<? extends OrderAggregate>> updateOrderAggregate(final PaymentCreateCommandContext context) {
    return orderAggregate -> {
      orderAggregate.setOrder(this.order);
      orderAggregate.setStatus(OrderAggregateStatus.PAYMENT_PROCESSED);
      return context.getRepository().save(orderAggregate);
    };
  }

  private Consumer<OrderAggregate> sendPaymentCreateCommand(final PaymentCreateCommandContext context) {
    return orderAggregate -> {
      final PaymentCreateCommandEvent paymentCreateCommandEvent = PaymentCreateCommandEvent.builder()
          .purchaseId(orderAggregate.getId())
          .price(orderAggregate.getOrder().getPrice())
          .userId(orderAggregate.getOrder().getUserId())
          .productId(orderAggregate.getOrder().getProductId())
          .sentAt(Instant.now())
          .build();
      context.getEventPublisher().tryPublish(paymentCreateCommandEvent);
    };
  }

}
