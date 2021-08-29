package com.xabe.orchestration.order.infrastructure.messaging;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.event.OrderCancelCommandEvent;
import com.xabe.orchestration.order.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestration.order.domain.repository.OrderRepository;
import com.xabe.orchestration.order.infrastructure.messaging.mapper.MessagingMapper;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class OrderEventConsumer implements EventConsumer {

  public static final String ORDER_CANCEL_COMMAND_EVENT = "OrderCancelCommandEvent";

  public static final String ORDER_CREATE_COMMAND_EVENT = "OrderCreateCommandEvent";

  public static final String ERROR = "ERROR";

  public static final String SUCCESS = "SUCCESS";

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
    this.mapHandlerEvent =
        Map.of(OrderCreateCommandEvent.class, this::orderCreateCommandEvent, OrderCancelCommandEvent.class, this::orderCancelCommandEvent);
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
    this.orderRepository.create(order).subscribe().with(
        this.sendEventSuccess(this.messagingMapper::toCreatedEvent),
        this.sendEventError(ORDER_CREATE_COMMAND_EVENT, order, this.messagingMapper::toCreatedEvent));
  }

  private void orderCancelCommandEvent(final Event event) {
    final OrderCancelCommandEvent orderCancelCommandEvent = OrderCancelCommandEvent.class.cast(event);
    final Order order = this.messagingMapper.toEntity(orderCancelCommandEvent);
    this.orderRepository.update(order.getId(), order).subscribe().with(
        this.sendEventSuccess(this.messagingMapper::toCanceledEvent),
        this.sendEventError(ORDER_CANCEL_COMMAND_EVENT, order, this.messagingMapper::toCanceledEvent));
  }

  private Consumer<Throwable> sendEventError(final String command, final Order order, final BiFunction<Order, String, Event> mapping) {
    return throwable -> {
      this.eventPublisher.tryPublish(mapping.apply(order, ERROR));
      this.logger.error("Error to save command {} with order: {}", command, order, throwable);
    };
  }

  private Consumer<Order> sendEventSuccess(final BiFunction<Order, String, Event> mapping) {
    return order -> {
      this.eventPublisher.tryPublish(mapping.apply(order, SUCCESS));
    };
  }
}
