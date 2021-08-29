package com.xabe.orchestration.order.infrastructure.config;

import com.xabe.avro.v1.OrderCancelCommand;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestation.common.infrastructure.event.EventHandler;
import com.xabe.orchestation.common.infrastructure.event.SimpleEventHandler;
import com.xabe.orchestration.order.infrastructure.messaging.OrderEventConsumer;
import com.xabe.orchestration.order.infrastructure.messaging.mapper.MessagingMapper;
import io.quarkus.arc.DefaultBean;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@ApplicationScoped
public class EventHandlerConfiguration {

  @Named("orderHandlers")
  @Produces
  @DefaultBean
  public Map<Class, EventHandler> orderEventHandlers(final OrderEventConsumer orderEventConsumer, final MessagingMapper messagingMapper) {
    final Map<Class, EventHandler> eventHandlers = new HashMap<>();
    eventHandlers.put(OrderCreateCommand.class,
        new SimpleEventHandler<>(messagingMapper::toAvroCreateCommandEvent, orderEventConsumer::consume));
    eventHandlers.put(OrderCancelCommand.class,
        new SimpleEventHandler<>(messagingMapper::toAvroCancelCommandEvent, orderEventConsumer::consume));
    return eventHandlers;
  }

}
