package com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.dispatch.CommandDispatcher;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestration.orchestrator.domain.command.shipping.ShippingCreateCommand;
import com.xabe.orchestration.orchestrator.domain.command.shipping.ShippingCreateCommandContext;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.payment.Payment;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCanceledEvent;
import com.xabe.orchestration.orchestrator.domain.event.payment.PaymentCreatedEvent;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.mapper.MessagingConsumerMapper;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@ApplicationScoped
@Named("PaymentEventConsumer")
public class PaymentEventConsumer implements EventConsumer {

  public static final String SUCCESS = "SUCCESS";

  private final Logger logger;

  private final MessagingConsumerMapper messagingConsumerMapper;

  private final CommandDispatcher<ShippingCreateCommandContext, OrderAggregate, String> shippingCreateCommandDispatcher;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public PaymentEventConsumer(
      final Logger logger,
      final MessagingConsumerMapper messagingConsumerMapper,
      final CommandDispatcher<ShippingCreateCommandContext, OrderAggregate, String> shippingCreateCommandDispatcher) {
    this.logger = logger;
    this.messagingConsumerMapper = messagingConsumerMapper;
    this.shippingCreateCommandDispatcher = shippingCreateCommandDispatcher;
    this.mapHandlerEvent =
        Map.of(PaymentCreatedEvent.class, this::paymentCreatedEvent, PaymentCanceledEvent.class, this::paymentCanceledEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void paymentCreatedEvent(final Event event) {
    final PaymentCreatedEvent paymentCreatedEvent = PaymentCreatedEvent.class.cast(event);
    final Payment payment = this.messagingConsumerMapper.toPaymentEntity(paymentCreatedEvent);
    if (SUCCESS.equalsIgnoreCase(paymentCreatedEvent.getOperationStatus())) {
      this.shippingCreateCommandDispatcher.dispatch(new ShippingCreateCommand(paymentCreatedEvent.getPurchaseId(), payment));
    } else {
      this.logger.error("Error to create payment {}", paymentCreatedEvent);
    }
  }

  private void paymentCanceledEvent(final Event event) {
    final PaymentCanceledEvent paymentCanceledEvent = PaymentCanceledEvent.class.cast(event);
    final Payment payment = this.messagingConsumerMapper.toPaymentEntity(paymentCanceledEvent);
    if (SUCCESS.equalsIgnoreCase(paymentCanceledEvent.getOperationStatus())) {
    } else {
      this.logger.error("Error to canceled order {}", paymentCanceledEvent);
    }
  }

}
