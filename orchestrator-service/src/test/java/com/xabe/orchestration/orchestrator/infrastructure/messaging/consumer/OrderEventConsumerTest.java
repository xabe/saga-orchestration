package com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.dispatch.CommandDispatcher;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestration.orchestrator.domain.command.payment.PaymentCreateCommand;
import com.xabe.orchestration.orchestrator.domain.command.payment.PaymentCreateCommandContext;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.entity.order.Order;
import com.xabe.orchestration.orchestrator.domain.event.order.OrderCreatedEvent;
import com.xabe.orchestration.orchestrator.infrastructure.OrderMother;
import com.xabe.orchestration.orchestrator.infrastructure.messaging.consumer.mapper.MessagingConsumerMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class OrderEventConsumerTest {

  private Logger logger;

  private CommandDispatcher<PaymentCreateCommandContext, OrderAggregate, String> commandDispatcher;

  private EventConsumer eventConsumer;

  @BeforeEach
  public void setUp() throws Exception {
    this.logger = mock(Logger.class);
    this.commandDispatcher = mock(CommandDispatcher.class);
    this.eventConsumer = new OrderEventConsumer(this.logger, new MessagingConsumerMapperImpl(), this.commandDispatcher);
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
    verify(this.commandDispatcher, never()).dispatch(any());
  }

  @Test
  public void givenAEventValidCreateWhenInvokeConsumeThenSendCommand() throws Exception {
    //Given
    final OrderCreatedEvent event = OrderMother.createOrderCreatedEvent("SUCCESS");
    final ArgumentCaptor<PaymentCreateCommand> argumentCaptor = ArgumentCaptor.forClass(PaymentCreateCommand.class);

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.commandDispatcher).dispatch(argumentCaptor.capture());
    final PaymentCreateCommand result = argumentCaptor.getValue();
    assertThat(result, is(notNullValue()));
    assertThat(result.getAggregateRootId(), is(event.getPurchaseId()));
    final Order order = result.getOrder();
    assertThat(order.getId(), is(event.getId()));
    assertThat(order.getUserId(), is(event.getUserId()));
    assertThat(order.getProductId(), is(event.getProductId()));
    assertThat(order.getCreatedAt().toInstant(), is(event.getCreatedAt()));
    assertThat(order.getPrice(), is(event.getPrice()));
    assertThat(order.getStatus().name(), is(event.getStatus()));
  }

  @Test
  public void givenAEventValidCreateErrorWhenInvokeConsumeThenNotSendCommand() throws Exception {
    //Given
    final OrderCreatedEvent event = OrderMother.createOrderCreatedEvent("ERROR");

    //When
    this.eventConsumer.consume(event);

    //Then
    verify(this.logger).error(anyString(), any(OrderCreatedEvent.class));
    verify(this.commandDispatcher, never()).dispatch(any());
  }

}