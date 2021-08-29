package com.xabe.orchestration.order.infrastructure.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.event.OrderCancelCommandEvent;
import com.xabe.orchestration.order.domain.event.OrderCanceledEvent;
import com.xabe.orchestration.order.domain.event.OrderCreateCommandEvent;
import com.xabe.orchestration.order.domain.event.OrderCreatedEvent;
import com.xabe.orchestration.order.domain.repository.OrderRepository;
import com.xabe.orchestration.order.infrastructure.OrderMother;
import com.xabe.orchestration.order.infrastructure.messaging.mapper.MessagingMapperImpl;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class OrderEventConsumerTest {

  private Logger logger;

  private OrderRepository orderRepository;

  private EventPublisher eventPublisher;

  private EventConsumer eventConsumer;

  @BeforeEach
  public void setUp() throws Exception {
    this.logger = mock(Logger.class);
    this.orderRepository = mock(OrderRepository.class);
    this.eventPublisher = mock(EventPublisher.class);
    this.eventConsumer = new OrderEventConsumer(this.logger, this.orderRepository, new MessagingMapperImpl(), this.eventPublisher);
  }

  @Test
  public void givenAEventNotValidWhenInvokeTryPublishThenIgnoreEvent() throws Exception {
    //Given
    final Event event = new Event() {
    };

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.logger).warn(anyString(), eq(event));
    verify(this.eventPublisher, never()).tryPublish(any());
  }

  @Test
  public void givenAEventValidCreateWhenInvokeTryPublishThenSendEvent() throws Exception {
    //Given
    final OrderCreateCommandEvent event = OrderMother.createOrderCreateCommandEvent();
    final ArgumentCaptor<OrderCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
    doAnswer(invocationOnMock -> {
      final Order order = Order.class.cast(invocationOnMock.getArguments()[0]).toBuilder().id(1L).build();
      return Uni.createFrom().item(order);
    }).when(this.orderRepository).create(any());

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.eventPublisher).tryPublish(argumentCaptor.capture());
    final OrderCreatedEvent result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(1L));
    assertThat(result.getPurchaseId(), is(event.getPurchaseId()));
    assertThat(result.getUserId(), is(event.getUserId()));
    assertThat(result.getProductId(), is(event.getProductId()));
    assertThat(result.getCreatedAt(), is(event.getSentAt()));
    assertThat(result.getPrice(), is(event.getPrice()));
    assertThat(result.getStatus(), is("CREATED"));
    assertThat(result.getOperationStatus(), is("SUCCESS"));
  }

  @Test
  public void givenAEventValidCreateWhenInvokeTryPublishThenSendEventError() throws Exception {
    //Given
    final OrderCreateCommandEvent event = OrderMother.createOrderCreateCommandEvent();
    final ArgumentCaptor<OrderCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
    when(this.orderRepository.create(any())).thenReturn(Uni.createFrom().failure(new RuntimeException()));

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.eventPublisher).tryPublish(argumentCaptor.capture());
    final OrderCreatedEvent result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(nullValue()));
    assertThat(result.getPurchaseId(), is(event.getPurchaseId()));
    assertThat(result.getUserId(), is(event.getUserId()));
    assertThat(result.getProductId(), is(event.getProductId()));
    assertThat(result.getCreatedAt(), is(event.getSentAt()));
    assertThat(result.getPrice(), is(event.getPrice()));
    assertThat(result.getStatus(), is("CREATED"));
    assertThat(result.getOperationStatus(), is("ERROR"));
  }

  @Test
  public void givenAEventValidCanceledWhenInvokeTryPublishThenSendEvent() throws Exception {
    //Given
    final OrderCancelCommandEvent event = OrderMother.createOrderCancelCommandEvent();
    final ArgumentCaptor<OrderCanceledEvent> argumentCaptor = ArgumentCaptor.forClass(OrderCanceledEvent.class);
    doAnswer(invocationOnMock -> {
      final Order order = Order.class.cast(invocationOnMock.getArguments()[1]).toBuilder().id(1L).price(10L).build();
      return Uni.createFrom().item(order);
    }).when(this.orderRepository).update(any(), any());

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.eventPublisher).tryPublish(argumentCaptor.capture());
    final OrderCanceledEvent result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(1L));
    assertThat(result.getPurchaseId(), is(event.getPurchaseId()));
    assertThat(result.getUserId(), is(event.getUserId()));
    assertThat(result.getProductId(), is(event.getProductId()));
    assertThat(result.getCreatedAt(), is(event.getSentAt()));
    assertThat(result.getPrice(), is(10L));
    assertThat(result.getStatus(), is("CANCELED"));
    assertThat(result.getOperationStatus(), is("SUCCESS"));
  }

  @Test
  public void givenAEventValidCancelWhenInvokeTryPublishThenSendEventError() throws Exception {
    //Given
    final OrderCancelCommandEvent event = OrderMother.createOrderCancelCommandEvent();
    final ArgumentCaptor<OrderCanceledEvent> argumentCaptor = ArgumentCaptor.forClass(OrderCanceledEvent.class);
    when(this.orderRepository.update(any(), any())).thenReturn(Uni.createFrom().failure(new RuntimeException()));

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.eventPublisher).tryPublish(argumentCaptor.capture());
    final OrderCanceledEvent result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getId(), is(event.getOrderId()));
    assertThat(result.getPurchaseId(), is(event.getPurchaseId()));
    assertThat(result.getUserId(), is(event.getUserId()));
    assertThat(result.getProductId(), is(event.getProductId()));
    assertThat(result.getCreatedAt(), is(event.getSentAt()));
    assertThat(result.getPrice(), is(nullValue()));
    assertThat(result.getStatus(), is("CANCELED"));
    assertThat(result.getOperationStatus(), is("ERROR"));
  }

}