package com.xabe.orchestration.shipping.infrastructure.messaging;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.shipping.domain.entity.Shipping;
import com.xabe.orchestration.shipping.domain.event.ShippingCancelCommandEvent;
import com.xabe.orchestration.shipping.domain.event.ShippingCreateCommandEvent;
import com.xabe.orchestration.shipping.domain.repository.ShippingRepository;
import com.xabe.orchestration.shipping.infrastructure.messaging.mapper.MessagingMapper;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class ShippingEventConsumer implements EventConsumer {

  public static final String SHIPPING_CANCEL_COMMAND_EVENT = "ShippingCancelCommandEvent";

  public static final String SHIPPING_CREATE_COMMAND_EVENT = "ShippingCreateCommandEvent";

  public static final String ERROR = "ERROR";

  public static final String SUCCESS = "SUCCESS";

  private final Logger logger;

  private final ShippingRepository shippingRepository;

  private final MessagingMapper messagingMapper;

  private final EventPublisher eventPublisher;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public ShippingEventConsumer(final Logger logger, final ShippingRepository shippingRepository, final MessagingMapper messagingMapper,
      final EventPublisher eventPublisher) {
    this.logger = logger;
    this.shippingRepository = shippingRepository;
    this.messagingMapper = messagingMapper;
    this.eventPublisher = eventPublisher;
    this.mapHandlerEvent =
        Map.of(ShippingCreateCommandEvent.class, this::orderCreateCommandEvent, ShippingCancelCommandEvent.class,
            this::orderCancelCommandEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void orderCreateCommandEvent(final Event event) {
    final ShippingCreateCommandEvent orderCreateCommandEvent = ShippingCreateCommandEvent.class.cast(event);
    final Shipping shipping = this.messagingMapper.toEntity(orderCreateCommandEvent);
    this.shippingRepository.create(shipping).subscribe().with(
        this.sendEventSuccess(this.messagingMapper::toCreatedEvent),
        this.sendEventError(SHIPPING_CREATE_COMMAND_EVENT, shipping, this.messagingMapper::toCreatedEvent));
  }

  private void orderCancelCommandEvent(final Event event) {
    final ShippingCancelCommandEvent orderCancelCommandEvent = ShippingCancelCommandEvent.class.cast(event);
    final Shipping shipping = this.messagingMapper.toEntity(orderCancelCommandEvent);
    this.shippingRepository.update(shipping.getId(), shipping).subscribe().with(
        this.sendEventSuccess(this.messagingMapper::toCanceledEvent),
        this.sendEventError(SHIPPING_CANCEL_COMMAND_EVENT, shipping, this.messagingMapper::toCanceledEvent));
  }

  private Consumer<Throwable> sendEventError(final String command, final Shipping order,
      final BiFunction<Shipping, String, Event> mapping) {
    return throwable -> {
      this.eventPublisher.tryPublish(mapping.apply(order, ERROR));
      this.logger.error("Error to save command {} with order: {}", command, order, throwable);
    };
  }

  private Consumer<Shipping> sendEventSuccess(final BiFunction<Shipping, String, Event> mapping) {
    return shipping -> {
      this.eventPublisher.tryPublish(mapping.apply(shipping, SUCCESS));
    };
  }
}
