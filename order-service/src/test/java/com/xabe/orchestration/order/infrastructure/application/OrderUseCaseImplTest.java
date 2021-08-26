package com.xabe.orchestration.order.infrastructure.application;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.repository.OrderRepository;
import io.smallrye.mutiny.Uni;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderUseCaseImplTest {

  private OrderRepository orderRepository;

  private OrderUseCase orderUseCase;

  @BeforeEach
  public void setUp() throws Exception {
    this.orderRepository = mock(OrderRepository.class);
    this.orderUseCase = new OrderUseCaseImpl(this.orderRepository);
  }

  @Test
  public void givenAIdWhenInvokeGetOrderThenReturnOrden() throws Exception {
    //Given
    final Long id = 1L;
    when(this.orderRepository.getOrder(id)).thenReturn(Uni.createFrom().item(Order.builder().build()));

    //When
    final Uni<Order> order = this.orderUseCase.getOrder(id);

    //Then
    assertThat(order, is(notNullValue()));
    final Order result = order.subscribeAsCompletionStage().get();
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void shouldGetAllOrders() throws Exception {
    //Given
    when(this.orderRepository.getOrders()).thenReturn(Uni.createFrom().item(List.of(Order.builder().build())));

    //When
    final Uni<List<Order>> orders = this.orderUseCase.getOrders();

    //Then
    assertThat(orders, is(notNullValue()));
    final List<Order> result = orders.subscribeAsCompletionStage().get();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(hasSize(1)));
  }

  @Test
  public void shouldCreateOrder() throws Exception {
    //Given
    final Order order = Order.builder().build();
    when(this.orderRepository.create(order)).thenReturn(Uni.createFrom().item(order));

    //When
    final Uni<Order> result = this.orderUseCase.create(order);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.subscribeAsCompletionStage().get(), is(notNullValue()));
  }

  @Test
  public void shouldUpdateOrder() throws Exception {
    //Given
    final Order order = Order.builder().build();
    final Long id = 1L;
    when(this.orderRepository.update(id, order)).thenReturn(Uni.createFrom().item(order));

    //When
    final Uni<Order> result = this.orderUseCase.update(id, order);

    //Then
    assertThat(result, is(notNullValue()));
    assertThat(result.subscribeAsCompletionStage().get(), is(notNullValue()));
  }
}