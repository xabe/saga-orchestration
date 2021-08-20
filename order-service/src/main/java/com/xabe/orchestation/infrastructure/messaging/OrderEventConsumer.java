package com.xabe.orchestation.infrastructure.messaging;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.domain.entity.OrderStatus;
import com.xabe.orchestation.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestation.domain.repository.OrderRepository;
import com.xabe.orchestation.infrastructure.Event;
import com.xabe.orchestation.infrastructure.event.EventConsumer;
import com.xabe.orchestation.infrastructure.event.EventPublisher;
import com.xabe.orchestation.infrastructure.messaging.mapper.OrderEventMapper;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class OrderEventConsumer implements EventConsumer {

  private final Logger logger;

  private final OrderRepository orderRepository;

  private final OrderEventMapper orderEventMapper;

  private final EventPublisher eventPublisher;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public OrderEventConsumer(final Logger logger, final OrderRepository orderRepository, final OrderEventMapper orderEventMapper,
      final EventPublisher eventPublisher) {
    this.logger = logger;
    this.orderRepository = orderRepository;
    this.orderEventMapper = orderEventMapper;
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
    final Order order = this.orderEventMapper.toOrderEntity(orderCreateCommandEvent);
    this.orderRepository.create(order).subscribe().with(this::sendOrderCreated, this.sendOrderNotCreated(order));
  }

  private Consumer<Throwable> sendOrderNotCreated(final Order order) {
    return throwable -> {
      this.eventPublisher.tryPublish(this.orderEventMapper.toOrderEvent(order.toBuilder().status(OrderStatus.REJECTED).build()));
      this.logger.error("Error to save Order: {}", order, throwable);
    };
  }

  private void sendOrderCreated(final Order order) {
    this.eventPublisher.tryPublish(this.orderEventMapper.toOrderEvent(order));
  }
}
