package com.xabe.orchestation.shipping.infrastructure.config;

import com.xabe.avro.v1.ShippingCreateCommand;
import com.xabe.orchestation.common.infrastructure.event.EventHandler;
import com.xabe.orchestation.common.infrastructure.event.SimpleEventHandler;
import com.xabe.orchestation.shipping.infrastructure.messaging.ShippingEventConsumer;
import com.xabe.orchestation.shipping.infrastructure.messaging.mapper.MessagingMapper;
import io.quarkus.arc.DefaultBean;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@ApplicationScoped
public class EventHandlerConfiguration {

  @Named("shipmentsHandlers")
  @Produces
  @DefaultBean
  public Map<Class, EventHandler> shipmentsEventHandlers(final ShippingEventConsumer shippingEventConsumer,
      final MessagingMapper messagingMapper) {
    final Map<Class, EventHandler> eventHandlers = new HashMap<>();
    eventHandlers.put(ShippingCreateCommand.class,
        new SimpleEventHandler<>(messagingMapper::toAvroCommandEvent, shippingEventConsumer::consume));
    return eventHandlers;
  }

}
