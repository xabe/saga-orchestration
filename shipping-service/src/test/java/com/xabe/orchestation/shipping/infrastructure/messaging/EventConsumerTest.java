package com.xabe.orchestation.shipping.infrastructure.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.xabe.avro.v1.MessageEnvelopeShipping;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.ShippingCreateCommand;
import com.xabe.orchestation.common.infrastructure.event.EventHandler;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.smallrye.reactive.messaging.kafka.commit.KafkaCommitHandler;
import io.smallrye.reactive.messaging.kafka.commit.KafkaIgnoreCommit;
import io.smallrye.reactive.messaging.kafka.fault.KafkaFailureHandler;
import io.smallrye.reactive.messaging.kafka.fault.KafkaIgnoreFailure;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class EventConsumerTest {

  private EventHandler eventHandler;

  private EventConsumer eventConsumer;

  @BeforeEach
  public void setUp() throws Exception {
    final Logger logger = mock(Logger.class);
    this.eventHandler = mock(EventHandler.class);
    this.eventConsumer = new EventConsumer(logger, Map.of(ShippingCreateCommand.class, this.eventHandler));
  }

  @Test
  public void shouldConsumeEvent() throws Exception {
    //Given
    final ShippingCreateCommand shippingCreateCommand =
        ShippingCreateCommand.newBuilder().setProductId("1").setUserId("2").setPurchaseId("3").setPrice(1L).setSentAt(Instant.now())
            .build();
    final MessageEnvelopeShipping messageEnvelopeShipping =
        MessageEnvelopeShipping.newBuilder().setMetadata(this.createMetaData()).setPayload(shippingCreateCommand)
            .build();
    final ConsumerRecord<String, MessageEnvelopeShipping> consumerRecord =
        new ConsumerRecord<>("topic", 1, 1L, "key", messageEnvelopeShipping);
    final KafkaCommitHandler kafkaCommitHandler = new KafkaIgnoreCommit();
    final KafkaFailureHandler kafkaFailureHandler = new KafkaIgnoreFailure("channel");
    final IncomingKafkaRecord<String, MessageEnvelopeShipping> incomingKafkaRecord =
        new IncomingKafkaRecord<>(consumerRecord, kafkaCommitHandler, kafkaFailureHandler, false, false);

    final CompletionStage result = this.eventConsumer.consumeKafka(incomingKafkaRecord);

    assertThat(result, is(notNullValue()));
    assertThat(result.toCompletableFuture().get(), is(nullValue()));
    verify(this.eventHandler).handle(any());
  }

  @Test
  public void notShouldHandlerEvent() throws Exception {
    final MessageEnvelopeShipping messageEnvelopeShipping =
        MessageEnvelopeShipping.newBuilder().setMetadata(this.createMetaData()).setPayload(mock(SpecificRecord.class))
            .build();
    final ConsumerRecord<String, MessageEnvelopeShipping> consumerRecord =
        new ConsumerRecord<>("topic", 1, 1L, "key", messageEnvelopeShipping);
    final KafkaCommitHandler kafkaCommitHandler = new KafkaIgnoreCommit();
    final KafkaFailureHandler kafkaFailureHandler = new KafkaIgnoreFailure("channel");
    final IncomingKafkaRecord<String, MessageEnvelopeShipping> incomingKafkaRecord =
        new IncomingKafkaRecord<>(consumerRecord, kafkaCommitHandler, kafkaFailureHandler, false, false);

    final CompletionStage result = this.eventConsumer.consumeKafka(incomingKafkaRecord);

    assertThat(result, is(notNullValue()));
    assertThat(result.toCompletableFuture().get(), is(nullValue()));
    verify(this.eventHandler, never()).handle(any());
  }

  private Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("order").setName("order").setAction("update").setVersion("vTest")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }


}