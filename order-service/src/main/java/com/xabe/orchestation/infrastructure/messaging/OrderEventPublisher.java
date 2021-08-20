package com.xabe.orchestation.infrastructure.messaging;

import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.Order;
import com.xabe.orchestation.domain.event.OrderCreatedEvent;
import com.xabe.orchestation.infrastructure.Event;
import com.xabe.orchestation.infrastructure.event.EventPublisher;
import com.xabe.orchestation.infrastructure.messaging.mapper.OrderEventMapper;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;

@ApplicationScoped
public class OrderEventPublisher implements EventPublisher {

  public static final String ORDER = "order";

  public static final String TEST = "test";

  public static final String CREATE = "create";

  private final Logger logger;

  private final OrderEventMapper orderEventMapper;

  private final Emitter<MessageEnvelopeStatus> statusEmitter;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public OrderEventPublisher(final Logger logger, final OrderEventMapper orderEventMapper,
      @Channel("status") final Emitter<MessageEnvelopeStatus> statusEmitter) {
    this.logger = logger;
    this.orderEventMapper = orderEventMapper;
    this.statusEmitter = statusEmitter;
    this.mapHandlerEvent = Map.of(OrderCreatedEvent.class, this::orderCreatedEvent);
  }

  @Override
  public void tryPublish(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void orderCreatedEvent(final Event event) {
    final OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.class.cast(event);
    final Order order = this.orderEventMapper.toAvroOrderEvent(orderCreatedEvent);
    final com.xabe.avro.v1.OrderCreatedEvent createdEvent = com.xabe.avro.v1.OrderCreatedEvent.newBuilder()
        .setOrder(order)
        .setUpdatedAt(Instant.now())
        .build();
    final MessageEnvelopeStatus messageEnvelopeStatus =
        MessageEnvelopeStatus.newBuilder().setMetadata(this.createMetaData()).setPayload(createdEvent).build();
    this.statusEmitter.send(Message.of(messageEnvelopeStatus, this.createMetaDataKafka(orderCreatedEvent.getId().toString())));
    this.logger.info("Send Event {}", messageEnvelopeStatus);
  }

  private Metadata createMetaData() {
    return Metadata.newBuilder().setDomain(ORDER).setName(ORDER).setAction(CREATE).setVersion(TEST)
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

  private org.eclipse.microprofile.reactive.messaging.Metadata createMetaDataKafka(final String key) {
    return org.eclipse.microprofile.reactive.messaging.Metadata.of(OutgoingKafkaRecordMetadata.builder().withKey(key).build());
  }
}
