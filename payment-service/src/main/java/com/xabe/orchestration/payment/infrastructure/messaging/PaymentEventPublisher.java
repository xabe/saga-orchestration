package com.xabe.orchestration.payment.infrastructure.messaging;

import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.Payment;
import com.xabe.avro.v1.PaymentOperationStatus;
import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.payment.domain.event.PaymentCanceledEvent;
import com.xabe.orchestration.payment.domain.event.PaymentCreatedEvent;
import com.xabe.orchestration.payment.infrastructure.messaging.mapper.MessagingMapper;
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
public class PaymentEventPublisher implements EventPublisher {

  public static final String PAYMENT = "payment";

  public static final String TEST = "test";

  public static final String CREATE = "create";

  private final Logger logger;

  private final MessagingMapper messagingMapper;

  private final Emitter<MessageEnvelopeStatus> statusEmitter;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public PaymentEventPublisher(final Logger logger, final MessagingMapper messagingMapper,
      @Channel("status") final Emitter<MessageEnvelopeStatus> statusEmitter) {
    this.logger = logger;
    this.messagingMapper = messagingMapper;
    this.statusEmitter = statusEmitter;
    this.mapHandlerEvent =
        Map.of(PaymentCreatedEvent.class, this::paymentCreatedEvent, PaymentCanceledEvent.class, this::paymentCanceledEvent);
  }

  @Override
  public void tryPublish(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void paymentCreatedEvent(final Event event) {
    final PaymentCreatedEvent paymentCreatedEvent = PaymentCreatedEvent.class.cast(event);
    final Payment payment = this.messagingMapper.toAvroEvent(paymentCreatedEvent);
    final com.xabe.avro.v1.PaymentCreatedEvent createdEvent = com.xabe.avro.v1.PaymentCreatedEvent.newBuilder()
        .setPayment(payment)
        .setOperationStatus(PaymentOperationStatus.valueOf(paymentCreatedEvent.getOperationStatus()))
        .setUpdatedAt(Instant.now())
        .build();
    final MessageEnvelopeStatus messageEnvelopeStatus =
        MessageEnvelopeStatus.newBuilder().setMetadata(this.createMetaData()).setPayload(createdEvent).build();
    this.statusEmitter.send(Message.of(messageEnvelopeStatus, this.createMetaDataKafka(paymentCreatedEvent.getId().toString())));
    this.logger.info("Send Event PaymentCreatedEvent {}", messageEnvelopeStatus);
  }

  private void paymentCanceledEvent(final Event event) {
    final PaymentCanceledEvent paymentCanceledEvent = PaymentCanceledEvent.class.cast(event);
    final Payment payment = this.messagingMapper.toAvroEvent(paymentCanceledEvent);
    final com.xabe.avro.v1.PaymentCanceledEvent canceledEvent = com.xabe.avro.v1.PaymentCanceledEvent.newBuilder()
        .setPayment(payment)
        .setUpdatedAt(Instant.now())
        .setOperationStatus(PaymentOperationStatus.valueOf(paymentCanceledEvent.getOperationStatus()))
        .build();
    final MessageEnvelopeStatus messageEnvelopeStatus =
        MessageEnvelopeStatus.newBuilder().setMetadata(this.createMetaData()).setPayload(canceledEvent).build();
    this.statusEmitter.send(Message.of(messageEnvelopeStatus, this.createMetaDataKafka(paymentCanceledEvent.getId().toString())));
    this.logger.info("Send Event PaymentCanceledEvent {}", messageEnvelopeStatus);
  }

  private Metadata createMetaData() {
    return Metadata.newBuilder().setDomain(PAYMENT).setName(PAYMENT).setAction(CREATE).setVersion(TEST)
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

  private org.eclipse.microprofile.reactive.messaging.Metadata createMetaDataKafka(final String key) {
    return org.eclipse.microprofile.reactive.messaging.Metadata.of(OutgoingKafkaRecordMetadata.builder().withKey(key).build());
  }
}
