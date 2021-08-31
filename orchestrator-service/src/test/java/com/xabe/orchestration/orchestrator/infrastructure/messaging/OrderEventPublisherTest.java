package com.xabe.orchestration.orchestrator.infrastructure.messaging;

class OrderEventPublisherTest {

  /*private Logger logger;

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
  public void givenAEventCreatedValidWhenInvokeTryPublishThenSendEvent() throws Exception {
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
    this.assertMessageEnvelopeStatus(result.getPayload(), event, "SUCCESS");
  }

  @Test
  public void givenAEventCanceledValidWhenInvokeTryPublishThenSendEvent() throws Exception {
    //Given
    final Event event = OrderMother.createOrderCanceledEvent();
    final ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

    //When
    this.eventPublisher.tryPublish(event);

    //Then
    verify(this.emitter).send(messageArgumentCaptor.capture());
    verify(this.logger).info(anyString(), any(MessageEnvelopeStatus.class));

    final Message<MessageEnvelopeStatus> result = messageArgumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    this.assertMetadata(result.getMetadata());
    this.assertMessageEnvelopeStatus(result.getPayload(), event, "ERROR");
  }

  private void assertMetadata(final Metadata metadata) {
    assertThat(metadata, is(notNullValue()));
    assertThat(metadata.get(OutgoingKafkaRecordMetadata.class).isPresent(), is(true));
    assertThat(((OutgoingKafkaRecordMetadata) metadata.get(OutgoingKafkaRecordMetadata.class).get()).getKey(), is("1"));
  }

  private void assertMessageEnvelopeStatus(final MessageEnvelopeStatus messageEnvelopeStatus, final Event event,
      final String operationStatus) {
    assertThat(messageEnvelopeStatus, is(notNullValue()));
    this.assertMetadata(messageEnvelopeStatus.getMetadata());
    if (operationStatus.equals("ERROR")) {
      this.assertCanceledPayload(messageEnvelopeStatus.getPayload(), event, operationStatus);
    } else {
      this.assertCreatedPayload(messageEnvelopeStatus.getPayload(), event, operationStatus);
    }
  }

  private void assertCreatedPayload(final Object payload, final Event event, final String operationStatus) {
    final OrderCreatedEvent orderCreatedEventAvro = OrderCreatedEvent.class.cast(payload);
    final com.xabe.orchestration.orchestrator.domain.event.OrderCreatedEvent orderCreatedEvent =
        com.xabe.orchestration.orchestrator.domain.event.OrderCreatedEvent.class.cast(event);
    assertThat(orderCreatedEventAvro, is(notNullValue()));
    assertThat(orderCreatedEventAvro.getUpdatedAt(), is(notNullValue()));
    assertThat(orderCreatedEventAvro.getOperationStatus().name(), is(operationStatus));
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

  private void assertCanceledPayload(final Object payload, final Event event, final String operationStatus) {
    final OrderCanceledEvent orderCanceledEventAvro = OrderCanceledEvent.class.cast(payload);
    final com.xabe.orchestration.orchestrator.domain.event.OrderCanceledEvent orderCanceledEvent =
        com.xabe.orchestration.orchestrator.domain.event.OrderCanceledEvent.class.cast(event);
    assertThat(orderCanceledEventAvro, is(notNullValue()));
    assertThat(orderCanceledEventAvro.getUpdatedAt(), is(notNullValue()));
    assertThat(orderCanceledEventAvro.getOperationStatus().name(), is(operationStatus));
    final Order order = orderCanceledEventAvro.getOrder();
    assertThat(order, is(notNullValue()));
    assertThat(order.getId(), is(orderCanceledEvent.getId()));
    assertThat(order.getPurchaseId(), is(orderCanceledEvent.getPurchaseId()));
    assertThat(order.getUserId(), is(orderCanceledEvent.getUserId()));
    assertThat(order.getProductId(), is(orderCanceledEvent.getProductId()));
    assertThat(order.getPrice(), is(orderCanceledEvent.getPrice()));
    assertThat(order.getStatus().name(), is(orderCanceledEvent.getStatus()));
    assertThat(order.getCreatedAt(), is(orderCanceledEvent.getCreatedAt()));
  }

  private void assertMetadata(final com.xabe.avro.v1.Metadata metadata) {
    assertThat(metadata.getDomain(), is("order"));
    assertThat(metadata.getName(), is("order"));
    assertThat(metadata.getAction(), is("create"));
    assertThat(metadata.getVersion(), is("test"));
    assertThat(metadata.getTimestamp(), is((notNullValue())));
  }*/

}