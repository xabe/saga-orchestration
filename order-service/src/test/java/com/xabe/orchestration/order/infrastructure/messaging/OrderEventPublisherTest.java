package com.xabe.orchestration.order.infrastructure.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.avro.v1.Order;
import com.xabe.avro.v1.OrderCreatedEvent;
import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.order.infrastructure.OrderMother;
import com.xabe.orchestration.order.infrastructure.messaging.mapper.MessagingMapperImpl;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class OrderEventPublisherTest {

  private Logger logger;

  private Emitter<MessageEnvelopeStatus> emitter;

  private EventPublisher eventPublisher;

  @BeforeEach
  public void setUp() throws Exception {
    this.logger = mock(Logger.class);
    this.emitter = mock(Emitter.class);
    this.eventPublisher = new OrderEventPublisher(this.logger, new MessagingMapperImpl(), this.emitter);
  }

  @Test
  public void givenAEventNotValidWhenInvokeTryPublishThenIgnoreEvent() throws Exception {
    //Given
    final Event event = new Event() {
    };

    //When
    this.eventPublisher.tryPublish(event);

    //Then
    verify(this.logger).warn(anyString(), eq(event));
  }

  @Test
  public void givenAEventValidWhenInvokeTryPublishThenSendEvent() throws Exception {
    //Given
    final Event event = OrderMother.createOrderCreatedEvent();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    //When
    this.eventPublisher.tryPublish(event);

    //Then
    verify(this.emitter).send(messageArgumentCaptor.capture());
    verify(this.logger).info(anyString(), any(MessageEnvelopeStatus.class));

    final Message<MessageEnvelopeStatus> result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    this.assertMetadata(result.getMetadata());
    this.assertMessageEnvelopeStatus(result.getPayload(), event);
  }

  private void assertMetadata(final Metadata metadata) {
    assertThat(metadata, is(notNullValue()));
    assertThat(metadata.get(OutgoingKafkaRecordMetadata.class).isPresent(), is(true));
    assertThat(((OutgoingKafkaRecordMetadata) metadata.get(OutgoingKafkaRecordMetadata.class).get()).getKey(), is("1"));
  }

  private void assertMessageEnvelopeStatus(final MessageEnvelopeStatus messageEnvelopeStatus, final Event event) {
    assertThat(messageEnvelopeStatus, is(notNullValue()));
    this.assertMetadata(messageEnvelopeStatus.getMetadata());
    this.assertPayload(messageEnvelopeStatus.getPayload(), event);
  }

  private void assertPayload(final Object payload, final Event event) {
    final OrderCreatedEvent orderCreatedEventAvro = OrderCreatedEvent.class.cast(payload);
    final com.xabe.orchestration.order.domain.event.OrderCreatedEvent orderCreatedEvent =
        com.xabe.orchestration.order.domain.event.OrderCreatedEvent.class.cast(event);
    assertThat(orderCreatedEventAvro, is(notNullValue()));
    assertThat(orderCreatedEventAvro.getUpdatedAt(), is(notNullValue()));
    final Order order = orderCreatedEventAvro.getOrder();
    assertThat(order, is(notNullValue()));
    assertThat(order.getId(), is(orderCreatedEvent.getId()));
    assertThat(order.getPurchaseId(), is(orderCreatedEvent.getPurchaseId()));
    assertThat(order.getUserId(), is(orderCreatedEvent.getUserId()));
    assertThat(order.getProductId(), is(orderCreatedEvent.getProductId()));
    assertThat(order.getPrice(), is(orderCreatedEvent.getPrice()));
    assertThat(order.getStatus().name(), is(orderCreatedEvent.getStatus()));
    assertThat(order.getCreatedAt(), is(orderCreatedEvent.getCreatedAt()));
  }

  private void assertMetadata(final com.xabe.avro.v1.Metadata metadata) {
    assertThat(metadata.getDomain(), is("order"));
    assertThat(metadata.getName(), is("order"));
    assertThat(metadata.getAction(), is("create"));
    assertThat(metadata.getVersion(), is("test"));
    assertThat(metadata.getTimestamp(), is((notNullValue())));
  }

}