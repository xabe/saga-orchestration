package com.xabe.orchestration.orchestrator.infrastructure.messaging.publisher;

import com.xabe.avro.v1.MessageEnvelopeShipping;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.ShippingCancelCommand;
import com.xabe.avro.v1.ShippingCreateCommand;
import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.shipping.ShippingCreateCommandEvent;
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
@Named("ShippingEventPublisher")
public class ShippingEventPublisher implements EventPublisher {

  public static final String SHIPPING = "shipping";

  public static final String TEST = "test";

  public static final String CREATE = "create";

  private final Logger logger;

  private final MessagingPublisherMapper messagingPublisherMapper;

  private final Emitter<MessageEnvelopeShipping> statusEmitter;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public ShippingEventPublisher(final Logger logger, final MessagingPublisherMapper messagingPublisherMapper,
      @Channel("shipments") final Emitter<MessageEnvelopeShipping> statusEmitter) {
    this.logger = logger;
    this.messagingPublisherMapper = messagingPublisherMapper;
    this.statusEmitter = statusEmitter;
    this.mapHandlerEvent =
        Map.of(ShippingCreateCommandEvent.class, this::shippingCreateCommandEvent, ShippingCancelCommandEvent.class,
            this::shippingCancelCommandEvent);
  }

  @Override
  public void tryPublish(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void shippingCreateCommandEvent(final Event event) {
    final ShippingCreateCommandEvent shippingCreateCommandEvent = ShippingCreateCommandEvent.class.cast(event);
    final ShippingCreateCommand shippingCreateCommand = this.messagingPublisherMapper.toAvroCommandEvent(shippingCreateCommandEvent);
    final MessageEnvelopeShipping messageEnvelopeStatus =
        MessageEnvelopeShipping.newBuilder().setMetadata(this.createMetaData()).setPayload(shippingCreateCommand).build();
    this.statusEmitter.send(Message.of(messageEnvelopeStatus, this.createMetaDataKafka(shippingCreateCommandEvent.getPurchaseId())));
    this.logger.info("Send Event ShippingCreateCommand {}", messageEnvelopeStatus);
  }

  private void shippingCancelCommandEvent(final Event event) {
    final ShippingCancelCommandEvent shippingCancelCommandEvent = ShippingCancelCommandEvent.class.cast(event);
    final ShippingCancelCommand shippingCancelCommand = this.messagingPublisherMapper.toAvroCommandEvent(shippingCancelCommandEvent);
    final MessageEnvelopeShipping messageEnvelopeStatus =
        MessageEnvelopeShipping.newBuilder().setMetadata(this.createMetaData()).setPayload(shippingCancelCommand).build();
    this.statusEmitter.send(Message.of(messageEnvelopeStatus, this.createMetaDataKafka(shippingCancelCommandEvent.getPurchaseId())));
    this.logger.info("Send Event ShippingCancelCommand {}", messageEnvelopeStatus);
  }

  private Metadata createMetaData() {
    return Metadata.newBuilder().setDomain(SHIPPING).setName(SHIPPING).setAction(CREATE).setVersion(TEST)
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

  private org.eclipse.microprofile.reactive.messaging.Metadata createMetaDataKafka(final String key) {
    return org.eclipse.microprofile.reactive.messaging.Metadata.of(OutgoingKafkaRecordMetadata.builder().withKey(key).build());
  }
}
