package com.xabe.orchestation.shipping.infrastructure.messaging;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestation.shipping.domain.entity.Shipping;
import com.xabe.orchestation.shipping.domain.entity.ShippingStatus;
import com.xabe.orchestation.shipping.domain.event.ShippingCreateCommandEvent;
import com.xabe.orchestation.shipping.domain.repository.ShippingRepository;
import com.xabe.orchestation.shipping.infrastructure.messaging.mapper.MessagingMapper;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class ShippingEventConsumer implements EventConsumer {

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
    this.mapHandlerEvent = Map.of(ShippingCreateCommandEvent.class, this::shippingCreateCommandEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void shippingCreateCommandEvent(final Event event) {
    final ShippingCreateCommandEvent shippingCreateCommandEvent = ShippingCreateCommandEvent.class.cast(event);
    final Shipping shipping = this.messagingMapper.toEntity(shippingCreateCommandEvent);
    this.shippingRepository.create(shipping).subscribe().with(this::sendOrderCreated, this.sendOrderNotCreated(shipping));
  }

  private Consumer<Throwable> sendOrderNotCreated(final Shipping shipping) {
    return throwable -> {
      this.eventPublisher.tryPublish(this.messagingMapper.toEvent(shipping.toBuilder().status(ShippingStatus.CANCELED).build()));
      this.logger.error("Error to save Order: {}", shipping, throwable);
    };
  }

  private void sendOrderCreated(final Shipping shipping) {
    this.eventPublisher.tryPublish(this.messagingMapper.toEvent(shipping));
  }
}
