package com.xabe.orchestration.orchestrator.infrastructure.messaging.publisher;

import com.xabe.avro.v1.MessageEnvelopeOrder;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.OrderCancelCommand;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreateCommandEvent;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.publisher.mapper.MessagingPublisherMapper;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;

@ApplicationScoped
@Named("OrderEventPublisher")
public class OrderEventPublisher implements EventPublisher {

  public static final String ORDER = "order";

  public static final String TEST = "test";

  public static final String CREATE = "create";

  private final Logger logger;

  private final MessagingPublisherMapper messagingPublisherMapper;

  private final Emitter<MessageEnvelopeOrder> orderEmitter;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public OrderEventPublisher(final Logger logger, final MessagingPublisherMapper messagingPublisherMapper,
      @Channel("orders") final Emitter<MessageEnvelopeOrder> orderEmitter) {
    this.logger = logger;
    this.messagingPublisherMapper = messagingPublisherMapper;
    this.orderEmitter = orderEmitter;
    this.mapHandlerEvent =
        Map.of(OrderCreateCommandEvent.class, this::orderCreateCommandEvent, OrderCancelCommandEvent.class, this::orderCancelCommandEvent);
  }

  @Override
  public void tryPublish(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void orderCreateCommandEvent(final Event event) {
    final OrderCreateCommandEvent orderCreateCommandEvent = OrderCreateCommandEvent.class.cast(event);
    final OrderCreateCommand orderCreateCommand = this.messagingPublisherMapper.toAvroCommandEvent(orderCreateCommandEvent);
    final MessageEnvelopeOrder messageEnvelopeOrder =
        MessageEnvelopeOrder.newBuilder().setMetadata(this.createMetaData()).setPayload(orderCreateCommand).build();
    this.orderEmitter.send(Message.of(messageEnvelopeOrder, this.createMetaDataKafka(orderCreateCommandEvent.getPurchaseId())));
    this.logger.info("Send Event OrderCreateCommand {}", messageEnvelopeOrder);
  }

  private void orderCancelCommandEvent(final Event event) {
    final OrderCancelCommandEvent orderCancelCommandEvent = OrderCancelCommandEvent.class.cast(event);
    final OrderCancelCommand orderCancelCommand = this.messagingPublisherMapper.toAvroCommandEvent(orderCancelCommandEvent);
    final MessageEnvelopeOrder messageEnvelopeOrder =
        MessageEnvelopeOrder.newBuilder().setMetadata(this.createMetaData()).setPayload(orderCancelCommand).build();
    this.orderEmitter.send(Message.of(messageEnvelopeOrder, this.createMetaDataKafka(orderCancelCommandEvent.getPurchaseId())));
    this.logger.info("Send Event OrderCancelCommand {}", messageEnvelopeOrder);
  }

  private Metadata createMetaData() {
    return Metadata.newBuilder().setDomain(ORDER).setName(ORDER).setAction(CREATE).setVersion(TEST)
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

  private org.eclipse.microprofile.reactive.messaging.Metadata createMetaDataKafka(final String key) {
    return org.eclipse.microprofile.reactive.messaging.Metadata.of(OutgoingKafkaRecordMetadata.builder().withKey(key).build());
  }
}
