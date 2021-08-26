package com.xabe.orchestration.order.infrastructure.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.infrastructure.OrderMother;
import com.xabe.orchestration.order.infrastructure.application.OrderUseCase;
import com.xabe.orchestration.order.infrastructure.presentation.mapper.PresentationMapperImpl;
import com.xabe.orchestration.order.infrastructure.presentation.payload.OrderPayload;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.time.OffsetDateTime;
import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.common.jaxrs.UriBuilderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderResourceTest {

  private OrderUseCase orderUseCase;

  private OrderResource orderResource;

  @BeforeEach
  public void setUp() throws Exception {
    this.orderUseCase = mock(OrderUseCase.class);
    this.orderResource = new OrderResource(this.orderUseCase, new PresentationMapperImpl());
  }

  @Test
  public void shouldGetAllOrders() throws Exception {
    //Given
    final Order order = Order.builder()
        .id(1L)
        .purchaseId("111")
        .price(1L)
        .productId("1")
        .userId("2")
        .createdAt(OffsetDateTime.now()).build();
    when(this.orderUseCase.getOrders()).thenReturn(Uni.createFrom().item(List.of(order)));

    //When
    final Uni<List<OrderPayload>> result = this.orderResource.getOrders();

    //Then
    assertThat(result, is(notNullValue()));
    final List<OrderPayload> orderDOS = result.subscribeAsCompletionStage().get();
    assertThat(orderDOS, is(notNullValue()));
    assertThat(orderDOS, is(hasSize(1)));
    final OrderPayload orderPayload = orderDOS.get(0);
    assertThat(orderPayload.getId(), is(order.getId()));
    assertThat(orderPayload.getPurchaseId(), is(order.getPurchaseId()));
    assertThat(orderPayload.getUserId(), is(order.getUserId()));
    assertThat(orderPayload.getProductId(), is(order.getProductId()));
    assertThat(orderPayload.getPrice(), is(order.getPrice()));
    assertThat(orderPayload.getStatus().name(), is(order.getStatus().name()));
    assertThat(orderPayload.getCreatedAt(), is(order.getCreatedAt()));
  }

  @Test
  public void shouldGetOrder() throws Exception {
    //Given
    final Long id = 1L;
    final Order order = OrderMother.createOrder();
    when(this.orderUseCase.getOrder(id)).thenReturn(Uni.createFrom().item(order));

    //When
    final Uni<OrderPayload> result = this.orderResource.getOrder(id);

    //Then
    assertThat(result, is(notNullValue()));
    final OrderPayload orderPayload = result.subscribeAsCompletionStage().get();
    assertThat(orderPayload, is(notNullValue()));
    assertThat(orderPayload.getId(), is(order.getId()));
    assertThat(orderPayload.getPurchaseId(), is(order.getPurchaseId()));
    assertThat(orderPayload.getUserId(), is(order.getUserId()));
    assertThat(orderPayload.getProductId(), is(order.getProductId()));
    assertThat(orderPayload.getPrice(), is(order.getPrice()));
    assertThat(orderPayload.getStatus().name(), is(order.getStatus().name()));
    assertThat(orderPayload.getCreatedAt(), is(order.getCreatedAt()));
  }

  @Test
  public void shouldGetErrorOrder() throws Exception {
    //Given
    final Long id = 1L;
    when(this.orderUseCase.getOrder(id)).thenReturn(Uni.createFrom().nullItem());

    //When
    final UniAssertSubscriber<OrderPayload> result =
        this.orderResource.getOrder(id).subscribe().withSubscriber(UniAssertSubscriber.create());

    //Then
    assertThat(result, is(notNullValue()));
    result.awaitFailure();
    result.assertFailedWith(NotFoundException.class);
  }

  @Test
  public void shouldCreatedOrder() throws Exception {
    //Given
    final OrderPayload orderPayload = OrderPayload.builder().productId("1").userId("2").price(1L).build();
    final Order order = Order.builder().productId("1").userId("2").price(1L).build();
    final UriInfo uriInfo = mock(UriInfo.class);
    final UriBuilder uriBuilder = new UriBuilderImpl();
    final Long id = 1L;
    when(this.orderUseCase.create(order)).thenReturn(Uni.createFrom().item(order.toBuilder().id(1L).build()));
    when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

    //When
    final Uni<Response> result = this.orderResource.create(orderPayload, uriInfo);

    //Then
    assertThat(result, is(notNullValue()));
    final Response response = result.subscribeAsCompletionStage().get();
    assertThat(response.getLocation(), is(notNullValue()));
    assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
    assertThat(response.getLocation(), is(notNullValue()));
    assertThat(response.getLocation(), is(new UriBuilderImpl().path(id.toString()).build()));
  }

  @Test
  public void shouldCreatedOrderError() throws Exception {
    //Given
    final OrderPayload orderPayload = OrderPayload.builder().productId("1").userId("2").price(1L).build();
    final Order order = Order.builder().productId("1").userId("2").price(1L).build();
    final UriInfo uriInfo = mock(UriInfo.class);
    final UriBuilder uriBuilder = new UriBuilderImpl();
    when(this.orderUseCase.create(order)).thenReturn(Uni.createFrom().failure(RuntimeException::new));
    when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

    //When
    final Uni<Response> result = this.orderResource.create(orderPayload, uriInfo);

    //Then
    assertThat(result, is(notNullValue()));
    final Response response = result.subscribeAsCompletionStage().get();
    assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
  }

  @Test
  public void shouldUpdateOrder() throws Exception {
    //Given
    final OrderPayload orderPayload = OrderPayload.builder().productId("1").userId("2").price(1L).build();
    final Order order = Order.builder().productId("1").userId("2").price(1L).build();
    final UriInfo uriInfo = mock(UriInfo.class);
    final UriBuilder uriBuilder = new UriBuilderImpl();
    final Long id = 1L;
    when(this.orderUseCase.update(id, order)).thenReturn(Uni.createFrom().item(order.toBuilder().id(1L).build()));
    when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

    //When
    final Uni<Response> result = this.orderResource.update(id, orderPayload, uriInfo);

    //Then
    assertThat(result, is(notNullValue()));
    final Response response = result.subscribeAsCompletionStage().get();
    assertThat(response.getLocation(), is(notNullValue()));
    assertThat(response.getStatus(), is(Status.NO_CONTENT.getStatusCode()));
    assertThat(response.getLocation(), is(notNullValue()));
    assertThat(response.getLocation(), is(new UriBuilderImpl().build()));
  }

  @Test
  public void shouldUpdatedOrderError() throws Exception {
    //Given
    final OrderPayload orderPayload = OrderPayload.builder().productId("1").userId("2").price(1L).build();
    final Order order = Order.builder().productId("1").userId("2").price(1L).build();
    final UriInfo uriInfo = mock(UriInfo.class);
    final UriBuilder uriBuilder = new UriBuilderImpl();
    final Long id = 1L;
    when(this.orderUseCase.update(id, order)).thenReturn(Uni.createFrom().failure(RuntimeException::new));
    when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

    //When
    final Uni<Response> result = this.orderResource.update(id, orderPayload, uriInfo);

    //Then
    assertThat(result, is(notNullValue()));
    final Response response = result.subscribeAsCompletionStage().get();
    assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
  }
}