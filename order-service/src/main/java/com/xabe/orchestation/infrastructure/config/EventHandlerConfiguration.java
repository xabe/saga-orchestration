package com.xabe.orchestation.infrastructure.config;

import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestation.infrastructure.event.EventHandler;
import com.xabe.orchestation.infrastructure.event.SimpleEventHandler;
import com.xabe.orchestation.infrastructure.messaging.OrderEventConsumer;
import com.xabe.orchestation.infrastructure.messaging.mapper.OrderEventMapper;
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
  public Map<Class, EventHandler> orderEventHandlers(final OrderEventConsumer orderEventConsumer, final OrderEventMapper orderEventMapper) {
    final Map<Class, EventHandler> eventHandlers = new HashMap<>();
    eventHandlers.put(OrderCreateCommand.class,
        new SimpleEventHandler<>(orderEventMapper::toAvroCommandEvent, orderEventConsumer::consume));
    return eventHandlers;
  }

}
