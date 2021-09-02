package com.xabe.orchestration.orchestrator.infrastructure.messaging.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.xabe.avro.v1.MessageEnvelopeOrder;
import com.xabe.avro.v1.OrderCancelCommand;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCancelCommandEvent;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreateCommandEvent;
import com.xabe.orchestration.orchestrator.infrastructure.OrderMother;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.publisher.mapper.MessagingPublisherMapperImpl;
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

  private Emitter<MessageEnvelopeOrder> emitter;

  private EventPublisher eventPublisher;

  @BeforeEach
  public void setUp() throws Exception {
    this.logger = mock(Logger.class);
    this.emitter = mock(Emitter.class);
    this.eventPublisher = new OrderEventPublisher(this.logger, new MessagingPublisherMapperImpl(), this.emitter);
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
  public void givenAEventCreatedValidWhenInvokeTryPublishThenSendEvent() throws Exception {
    //Given
    final OrderCreateCommandEvent event = OrderMother.createOrderCreateCommandEvent();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    //When
    this.eventPublisher.tryPublish(event);

    //Then
    verify(this.emitter).send(messageArgumentCaptor.capture());
    verify(this.logger).info(anyString(), any(MessageEnvelopeOrder.class));

    final Message<MessageEnvelopeOrder> result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    this.assertMetadata(result.getMetadata());
    this.assertMessageEnvelopeOrder(result.getPayload(), event, "SUCCESS");
  }

  @Test
  public void givenAEventCanceledValidWhenInvokeTryPublishThenSendEvent() throws Exception {
    //Given
    final Event event = OrderMother.createOrderCancelCommandEvent();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    //When
    this.eventPublisher.tryPublish(event);

    //Then
    verify(this.emitter).send(messageArgumentCaptor.capture());
    verify(this.logger).info(anyString(), any(MessageEnvelopeOrder.class));

    final Message<MessageEnvelopeOrder> result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    this.assertMetadata(result.getMetadata());
    this.assertMessageEnvelopeOrder(result.getPayload(), event, "ERROR");
  }

  private void assertMetadata(final Metadata metadata) {
    assertThat(metadata, is(notNullValue()));
    assertThat(metadata.get(OutgoingKafkaRecordMetadata.class).isPresent(), is(true));
    assertThat(((OutgoingKafkaRecordMetadata) metadata.get(OutgoingKafkaRecordMetadata.class).get()).getKey(), is("3"));
  }

  private void assertMessageEnvelopeOrder(final MessageEnvelopeOrder messageEnvelopeOrder, final Event event,
      final String operationStatus) {
    assertThat(messageEnvelopeOrder, is(notNullValue()));
    this.assertMetadata(messageEnvelopeOrder.getMetadata());
    if (operationStatus.equals("ERROR")) {
      this.assertCanceledPayload(messageEnvelopeOrder.getPayload(), event);
    } else {
      this.assertCreatedPayload(messageEnvelopeOrder.getPayload(), event);
    }
  }

  private void assertCreatedPayload(final Object payload, final Event event) {
    final OrderCreateCommand orderCreateCommand = OrderCreateCommand.class.cast(payload);
    final OrderCreateCommandEvent orderCreateCommandEvent = OrderCreateCommandEvent.class.cast(event);
    assertThat(orderCreateCommand, is(notNullValue()));
    assertThat(orderCreateCommand.getPurchaseId(), is(orderCreateCommandEvent.getPurchaseId()));
    assertThat(orderCreateCommand.getUserId(), is(orderCreateCommandEvent.getUserId()));
    assertThat(orderCreateCommand.getProductId(), is(orderCreateCommandEvent.getProductId()));
    assertThat(orderCreateCommand.getPrice(), is(orderCreateCommandEvent.getPrice()));
    assertThat(orderCreateCommand.getSentAt(), is(orderCreateCommandEvent.getSentAt()));
  }

  private void assertCanceledPayload(final Object payload, final Event event) {
    final OrderCancelCommand orderCancelCommand = OrderCancelCommand.class.cast(payload);
    final OrderCancelCommandEvent orderCancelCommandEvent = OrderCancelCommandEvent.class.cast(event);
    assertThat(orderCancelCommand, is(notNullValue()));
    assertThat(orderCancelCommand.getPurchaseId(), is(orderCancelCommandEvent.getPurchaseId()));
    assertThat(orderCancelCommand.getUserId(), is(orderCancelCommandEvent.getUserId()));
    assertThat(orderCancelCommand.getProductId(), is(orderCancelCommandEvent.getProductId()));
    assertThat(orderCancelCommand.getOrderId(), is(orderCancelCommandEvent.getOrderId()));
    assertThat(orderCancelCommand.getSentAt(), is(orderCancelCommandEvent.getSentAt()));
  }

  private void assertMetadata(final com.xabe.avro.v1.Metadata metadata) {
    assertThat(metadata.getDomain(), is("order"));
    assertThat(metadata.getName(), is("order"));
    assertThat(metadata.getAction(), is("create"));
    assertThat(metadata.getVersion(), is("test"));
    assertThat(metadata.getTimestamp(), is((notNullValue())));
  }

}