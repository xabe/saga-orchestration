package com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer;

import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.orchestation.common.infrastructure.event.EventHandler;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class EventConsumer {

  private final Logger logger;

  private final Map<Class, EventHandler> handlers;

  @Incoming("status")
  public CompletionStage<Void> consumeKafka(final IncomingKafkaRecord<String, MessageEnvelopeStatus> message) {
    final Metadata metadata = message.getMetadata();
    this.logger.info("Received a message. message: {} metadata {}", message, metadata);
    final MessageEnvelopeStatus messageEnvelopeStatus = message.getPayload();
    final Class<?> msgClass = messageEnvelopeStatus.getPayload().getClass();
    final SpecificRecord payload = SpecificRecord.class.cast(messageEnvelopeStatus.getPayload());
    final EventHandler handler = this.handlers.get(msgClass);
    if (Objects.isNull(handler)) {
      this.logger.warn("Received a non supported message. Type: {}, toString: {}", msgClass.getName(), payload);
    } else {
      handler.handle(payload);
      this.logger.debug("Received a message. payload: {} metadata {}", payload, metadata);
    }
    return message.ack();
  }

}
