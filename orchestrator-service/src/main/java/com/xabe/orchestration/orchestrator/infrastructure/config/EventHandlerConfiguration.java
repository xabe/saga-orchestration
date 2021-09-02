package com.xabe.orchestration.orchestrator.infrastructure.config;

import com.xabe.avro.v1.OrderCanceledEvent;
import com.xabe.avro.v1.OrderCreatedEvent;
import com.xabe.avro.v1.PaymentCanceledEvent;
import com.xabe.avro.v1.PaymentCreatedEvent;
import com.xabe.avro.v1.ShippingCanceledEvent;
import com.xabe.avro.v1.ShippingCreatedEvent;
import com.xabe.orchestation.common.infrastructure.event.EventHandler;
import com.xabe.orchestation.common.infrastructure.event.SimpleEventHandler;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.OrderEventConsumer;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.PaymentEventConsumer;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.ShippingEventConsumer;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.mapper.MessagingConsumerMapper;
import io.quarkus.arc.DefaultBean;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@ApplicationScoped
public class EventHandlerConfiguration {

  @Named("sagaHandlers")
  @Produces
  @DefaultBean
  public Map<Class, EventHandler> sagaEventHandlers(
      final OrderEventConsumer orderEventConsumer,
      final PaymentEventConsumer paymentEventConsumer,
      final ShippingEventConsumer shippingEventConsumer,
      final MessagingConsumerMapper messagingConsumerMapper) {
    final Map<Class, EventHandler> eventHandlers = new HashMap<>();
    eventHandlers.put(OrderCreatedEvent.class,
        new SimpleEventHandler<>(messagingConsumerMapper::toAvroOrderCreatedEvent, orderEventConsumer::consume));
    eventHandlers.put(OrderCanceledEvent.class,
        new SimpleEventHandler<>(messagingConsumerMapper::toAvroOrderCanceledEvent, orderEventConsumer::consume));
    eventHandlers.put(PaymentCreatedEvent.class,
        new SimpleEventHandler<>(messagingConsumerMapper::toAvroPaymentCreatedEvent, paymentEventConsumer::consume));
    eventHandlers.put(PaymentCanceledEvent.class,
        new SimpleEventHandler<>(messagingConsumerMapper::toAvroPaymentCanceledEvent, paymentEventConsumer::consume));
    eventHandlers.put(ShippingCreatedEvent.class,
        new SimpleEventHandler<>(messagingConsumerMapper::toAvroShippingCreatedEvent, shippingEventConsumer::consume));
    eventHandlers.put(ShippingCanceledEvent.class,
        new SimpleEventHandler<>(messagingConsumerMapper::toAvroShippingCanceledEvent, shippingEventConsumer::consume));
    return eventHandlers;
  }

}
