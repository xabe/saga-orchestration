package com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregateStatus;
import com.xabe.orchestration.orchestrator.domain.entity.shipping.Shipping;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCanceledEvent;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCreatedEvent;
import com.xabe.orchestration.orchestrator.domain.repository.OrderRepository;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.mapper.MessagingConsumerMapper;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;

@ApplicationScoped
@Named("ShippingEventConsumer")
public class ShippingEventConsumer implements EventConsumer {

  public static final String SUCCESS = "SUCCESS";

  private final Logger logger;

  private final MessagingConsumerMapper messagingConsumerMapper;

  private final OrderRepository orderRepository;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public ShippingEventConsumer(
      final Logger logger,
      final MessagingConsumerMapper messagingConsumerMapper,
      final OrderRepository orderRepository) {
    this.logger = logger;
    this.messagingConsumerMapper = messagingConsumerMapper;
    this.orderRepository = orderRepository;
    this.mapHandlerEvent =
        Map.of(ShippingCreatedEvent.class, this::shippingCreatedEvent, ShippingCanceledEvent.class, this::shippingCanceledEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void shippingCreatedEvent(final Event event) {
    final ShippingCreatedEvent shippingCreatedEvent = ShippingCreatedEvent.class.cast(event);
    final Shipping shipping = this.messagingConsumerMapper.toShippingEntity(shippingCreatedEvent);
    if (SUCCESS.equalsIgnoreCase(shippingCreatedEvent.getOperationStatus())) {
      this.orderRepository.load(shippingCreatedEvent.getPurchaseId())
          .flatMap(this.updateOrderAggregate(shipping))
          .subscribe()
          .with(this.successUpdateOrderAggregate(),
              throwable -> this.logger.error("Error to save orderAggregate with shipping create command", throwable));
    } else {
      this.logger.error("Error to create shipping {}", shippingCreatedEvent);
    }
  }

  private Consumer<OrderAggregate> successUpdateOrderAggregate() {
    return orderAggregate -> {
      final OffsetDateTime now = OffsetDateTime.now();
      this.logger.info("End Saga orderAggregate {} time {} duration {} seconds", orderAggregate.getId(),
          DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now), Duration.between(orderAggregate.getCreatedAt(), now).getSeconds());
    };
  }

  private Function<OrderAggregate, Uni<? extends OrderAggregate>> updateOrderAggregate(final Shipping shipping) {
    return orderAggregate -> {
      orderAggregate.setShipping(shipping);
      orderAggregate.setStatus(OrderAggregateStatus.SUCCESS);
      return this.orderRepository.save(orderAggregate);
    };
  }

  private void shippingCanceledEvent(final Event event) {
    final ShippingCanceledEvent shippingCanceledEvent = ShippingCanceledEvent.class.cast(event);
    final Shipping shipping = this.messagingConsumerMapper.toShippingEntity(shippingCanceledEvent);
    if (SUCCESS.equalsIgnoreCase(shippingCanceledEvent.getOperationStatus())) {
    } else {
      this.logger.error("Error to canceled order {}", shippingCanceledEvent);
    }
  }

}
