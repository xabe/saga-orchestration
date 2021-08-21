package com.xabe.orchestration.order.infrastructure.messaging;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.entity.OrderStatus;
import com.xabe.orchestration.order.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestration.order.domain.repository.OrderRepository;
import com.xabe.orchestration.order.infrastructure.messaging.mapper.MessagingMapper;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class OrderEventConsumer implements EventConsumer {

  private final Logger logger;

  private final OrderRepository orderRepository;

  private final MessagingMapper messagingMapper;

  private final EventPublisher eventPublisher;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public OrderEventConsumer(final Logger logger, final OrderRepository orderRepository, final MessagingMapper messagingMapper,
      final EventPublisher eventPublisher) {
    this.logger = logger;
    this.orderRepository = orderRepository;
    this.messagingMapper = messagingMapper;
    this.eventPublisher = eventPublisher;
    this.mapHandlerEvent = Map.of(OrderCreateCommandEvent.class, this::orderCreateCommandEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void orderCreateCommandEvent(final Event event) {
    final OrderCreateCommandEvent orderCreateCommandEvent = OrderCreateCommandEvent.class.cast(event);
    final Order order = this.messagingMapper.toEntity(orderCreateCommandEvent);
    this.orderRepository.create(order).subscribe().with(this::sendOrderCreated, this.sendOrderNotCreated(order));
  }

  private Consumer<Throwable> sendOrderNotCreated(final Order order) {
    return throwable -> {
      this.eventPublisher.tryPublish(this.messagingMapper.toEvent(order.toBuilder().status(OrderStatus.REJECTED).build()));
      this.logger.error("Error to save Order: {}", order, throwable);
    };
  }

  private void sendOrderCreated(final Order order) {
    this.eventPublisher.tryPublish(this.messagingMapper.toEvent(order));
  }
}
