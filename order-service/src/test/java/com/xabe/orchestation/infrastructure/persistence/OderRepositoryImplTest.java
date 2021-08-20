package com.xabe.orchestation.infrastructure.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.domain.repository.OrderRepository;
import com.xabe.orchestation.infrastructure.OrderMother;
import com.xabe.orchestation.infrastructure.persistence.dto.OrderDTO;
import com.xabe.orchestation.infrastructure.persistence.mapper.OrderDTOMaperImpl;
import io.smallrye.mutiny.Uni;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class OderRepositoryImplTest {

  private OrderRepositoryPanache orderRepositoryPanache;

  private OrderRepository orderRepository;

  @BeforeEach
  public void setUp() throws Exception {
    final Logger logger = mock(Logger.class);
    this.orderRepositoryPanache = mock(OrderRepositoryPanache.class);
    this.orderRepository = new OderRepositoryImpl(logger, new OrderDTOMaperImpl(), this.orderRepositoryPanache);
  }

  @Test
  public void shouldGetOrder() throws Exception {
    //Given
    final Long id = 1L;
    final OrderDTO orderDTO = OrderMother.createOrderDTO();
    when(this.orderRepositoryPanache.findById(id)).thenReturn(Uni.createFrom().item(orderDTO));

    //When
    final Uni<Order> result = this.orderRepository.getOrder(id);

    //Then
    assertThat(result, is(notNullValue()));
    final Order order = result.subscribeAsCompletionStage().get();
    assertThat(order.getId(), is(orderDTO.getId()));
    assertThat(order.getPurchaseId(), is(orderDTO.getPurchaseId()));
    assertThat(order.getUserId(), is(orderDTO.getUserId()));
    assertThat(order.getProductId(), is(orderDTO.getProductId()));
    assertThat(order.getPrice(), is(orderDTO.getPrice()));
    assertThat(order.getStatus().name(), is(orderDTO.getStatus().name()));
    assertThat(order.getCreatedAt(), is(orderDTO.getCreatedAt()));
  }

  @Test
  public void shouldGetAllOrders() throws Exception {
    //Given
    final OrderDTO orderDTO = OrderMother.createOrderDTO();
    when(this.orderRepositoryPanache.listAll()).thenReturn(Uni.createFrom().item(List.of(orderDTO)));

    //When
    final Uni<List<Order>> result = this.orderRepository.getOrders();

    //Then
    assertThat(result, is(notNullValue()));
    final List<Order> orders = result.subscribeAsCompletionStage().get();
    assertThat(orders, is(notNullValue()));
    assertThat(orders, is(hasSize(1)));
    final Order order = orders.get(0);
    assertThat(order.getId(), is(orderDTO.getId()));
    assertThat(order.getPurchaseId(), is(orderDTO.getPurchaseId()));
    assertThat(order.getUserId(), is(orderDTO.getUserId()));
    assertThat(order.getProductId(), is(orderDTO.getProductId()));
    assertThat(order.getPrice(), is(orderDTO.getPrice()));
    assertThat(order.getStatus().name(), is(orderDTO.getStatus().name()));
    assertThat(order.getCreatedAt(), is(orderDTO.getCreatedAt()));
  }

  @Test
  public void shouldCreateOrder() throws Exception {
    //Given
    final Order order = OrderMother.createOrder();
    final OrderDTO orderDTO = OrderMother.createOrderDTO();
    final ArgumentCaptor<OrderDTO> argumentCaptor = ArgumentCaptor.forClass(OrderDTO.class);
    when(this.orderRepositoryPanache.persistAndFlush(argumentCaptor.capture())).thenReturn(Uni.createFrom().item(orderDTO));

    //When
    final Uni<Order> result = this.orderRepository.create(order);

    //Then
    assertThat(result, is(notNullValue()));
    final Order orderResult = result.subscribeAsCompletionStage().get();
    assertThat(orderResult.getId(), is(orderDTO.getId()));
    assertThat(orderResult.getPurchaseId(), is(orderDTO.getPurchaseId()));
    assertThat(orderResult.getUserId(), is(orderDTO.getUserId()));
    assertThat(orderResult.getProductId(), is(orderDTO.getProductId()));
    assertThat(orderResult.getPrice(), is(orderDTO.getPrice()));
    assertThat(orderResult.getStatus().name(), is(orderDTO.getStatus().name()));
    assertThat(orderResult.getCreatedAt(), is(orderDTO.getCreatedAt()));
    final OrderDTO value = argumentCaptor.getValue();
    assertThat(value.getId(), is(order.getId()));
    assertThat(value.getPurchaseId(), is(order.getPurchaseId()));
    assertThat(value.getUserId(), is(order.getUserId()));
    assertThat(value.getProductId(), is(order.getProductId()));
    assertThat(value.getPrice(), is(order.getPrice()));
    assertThat(value.getStatus().name(), is(order.getStatus().name()));
    assertThat(value.getCreatedAt(), is(order.getCreatedAt()));
    ;
  }

}