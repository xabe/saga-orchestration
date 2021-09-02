package com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.dispatch.CommandDispatcher;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestration.orchestrator.domain.command.payment.PaymentCreateCommand;
import com.xabe.orchestration.orchestrator.domain.command.payment.PaymentCreateCommandContext;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.order.Order;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCanceledEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreatedEvent;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.mapper.MessagingConsumerMapper;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@ApplicationScoped
@Named("OrderEventConsumer")
public class OrderEventConsumer implements EventConsumer {

  public static final String SUCCESS = "SUCCESS";

  private final Logger logger;

  private final MessagingConsumerMapper messagingConsumerMapper;

  private final CommandDispatcher<PaymentCreateCommandContext, OrderAggregate, String> paymentCreateCommandDispatcher;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public OrderEventConsumer(
      final Logger logger,
      final MessagingConsumerMapper messagingConsumerMapper,
      final CommandDispatcher<PaymentCreateCommandContext, OrderAggregate, String> paymentCreateCommandDispatcher) {
    this.logger = logger;
    this.messagingConsumerMapper = messagingConsumerMapper;
    this.paymentCreateCommandDispatcher = paymentCreateCommandDispatcher;
    this.mapHandlerEvent =
        Map.of(OrderCreatedEvent.class, this::orderCreatedEvent, OrderCanceledEvent.class, this::orderCanceledEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void orderCreatedEvent(final Event event) {
    final OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.class.cast(event);
    final Order order = this.messagingConsumerMapper.toOrderEntity(orderCreatedEvent);
    if (SUCCESS.equalsIgnoreCase(orderCreatedEvent.getOperationStatus())) {
      this.paymentCreateCommandDispatcher.dispatch(new PaymentCreateCommand(orderCreatedEvent.getPurchaseId(), order));
    } else {
      this.logger.error("Error to create order {}", orderCreatedEvent);
    }
  }

  private void orderCanceledEvent(final Event event) {
    final OrderCanceledEvent orderCanceledEvent = OrderCanceledEvent.class.cast(event);
    final Order order = this.messagingConsumerMapper.toOrderEntity(orderCanceledEvent);
    if (SUCCESS.equalsIgnoreCase(orderCanceledEvent.getOperationStatus())) {
    } else {
      this.logger.error("Error to canceled order {}", orderCanceledEvent);
    }
  }

}
