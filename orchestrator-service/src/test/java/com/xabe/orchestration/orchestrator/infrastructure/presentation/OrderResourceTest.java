package com.xabe.orchestration.orchestrator.infrastructure.presentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.infrastructure.OrderMother;
import com.xabe.orchestration.orchestrator.infrastructure.application.OrderUseCase;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.mapper.PresentationMapperImpl;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderAggregatePayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderRequestPayload;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
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
    final OrderAggregate orderAggregate = OrderMother.createOrderAggregate();
    when(this.orderUseCase.getOrders()).thenReturn(Uni.createFrom().item(List.of(orderAggregate)));

    //When
    final Uni<List<OrderAggregatePayload>> result = this.orderResource.getOrders();

    //Then
    assertThat(result, is(notNullValue()));
    final List<OrderAggregatePayload> orderAggregatePayloads = result.subscribeAsCompletionStage().get();
    assertThat(orderAggregatePayloads, is(notNullValue()));
    assertThat(orderAggregatePayloads, is(hasSize(1)));
    final OrderAggregatePayload orderAggregatePayload = orderAggregatePayloads.get(0);
    assertThat(orderAggregatePayload.getId(), is(orderAggregate.getId()));
    assertThat(orderAggregatePayload.getStatus().name(), is(orderAggregate.getStatus().name()));
    assertThat(orderAggregatePayload.getCreatedAt(), is(orderAggregate.getCreatedAt()));
    assertThat(orderAggregatePayload.getOrder().getUserId(), is(orderAggregate.getOrder().getUserId()));
    assertThat(orderAggregatePayload.getOrder().getProductId(), is(orderAggregate.getOrder().getProductId()));
    assertThat(orderAggregatePayload.getOrder().getPrice(), is(orderAggregate.getOrder().getPrice()));
    assertThat(orderAggregatePayload.getOrder().getStatus().name(), is(orderAggregate.getOrder().getStatus().name()));
    assertThat(orderAggregatePayload.getOrder().getCreatedAt(), is(orderAggregate.getOrder().getCreatedAt()));
    assertThat(orderAggregatePayload.getPayment().getUserId(), is(orderAggregate.getPayment().getUserId()));
    assertThat(orderAggregatePayload.getPayment().getProductId(), is(orderAggregate.getPayment().getProductId()));
    assertThat(orderAggregatePayload.getPayment().getPrice(), is(orderAggregate.getPayment().getPrice()));
    assertThat(orderAggregatePayload.getPayment().getStatus().name(), is(orderAggregate.getPayment().getStatus().name()));
    assertThat(orderAggregatePayload.getPayment().getCreatedAt(), is(orderAggregate.getPayment().getCreatedAt()));
    assertThat(orderAggregatePayload.getShipping().getUserId(), is(orderAggregate.getShipping().getUserId()));
    assertThat(orderAggregatePayload.getShipping().getProductId(), is(orderAggregate.getShipping().getProductId()));
    assertThat(orderAggregatePayload.getShipping().getPrice(), is(orderAggregate.getShipping().getPrice()));
    assertThat(orderAggregatePayload.getShipping().getStatus().name(), is(orderAggregate.getShipping().getStatus().name()));
    assertThat(orderAggregatePayload.getShipping().getCreatedAt(), is(orderAggregate.getShipping().getCreatedAt()));
  }

  @Test
  public void shouldGetOrder() throws Exception {
    //Given
    final String id = "1";
    final OrderAggregate orderAggregate = OrderMother.createOrderAggregate();
    when(this.orderUseCase.getOrder(id)).thenReturn(Uni.createFrom().item(orderAggregate));

    //When
    final Uni<OrderAggregatePayload> result = this.orderResource.getOrder(id);

    //Then
    assertThat(result, is(notNullValue()));
    final OrderAggregatePayload orderAggregatePayload = result.subscribeAsCompletionStage().get();
    assertThat(orderAggregatePayload, is(notNullValue()));
    assertThat(orderAggregatePayload.getId(), is(orderAggregate.getId()));
    assertThat(orderAggregatePayload.getStatus().name(), is(orderAggregate.getStatus().name()));
    assertThat(orderAggregatePayload.getCreatedAt(), is(orderAggregate.getCreatedAt()));
    assertThat(orderAggregatePayload.getOrder().getUserId(), is(orderAggregate.getOrder().getUserId()));
    assertThat(orderAggregatePayload.getOrder().getProductId(), is(orderAggregate.getOrder().getProductId()));
    assertThat(orderAggregatePayload.getOrder().getPrice(), is(orderAggregate.getOrder().getPrice()));
    assertThat(orderAggregatePayload.getOrder().getStatus().name(), is(orderAggregate.getOrder().getStatus().name()));
    assertThat(orderAggregatePayload.getOrder().getCreatedAt(), is(orderAggregate.getOrder().getCreatedAt()));
    assertThat(orderAggregatePayload.getPayment().getUserId(), is(orderAggregate.getPayment().getUserId()));
    assertThat(orderAggregatePayload.getPayment().getProductId(), is(orderAggregate.getPayment().getProductId()));
    assertThat(orderAggregatePayload.getPayment().getPrice(), is(orderAggregate.getPayment().getPrice()));
    assertThat(orderAggregatePayload.getPayment().getStatus().name(), is(orderAggregate.getPayment().getStatus().name()));
    assertThat(orderAggregatePayload.getPayment().getCreatedAt(), is(orderAggregate.getPayment().getCreatedAt()));
    assertThat(orderAggregatePayload.getShipping().getUserId(), is(orderAggregate.getShipping().getUserId()));
    assertThat(orderAggregatePayload.getShipping().getProductId(), is(orderAggregate.getShipping().getProductId()));
    assertThat(orderAggregatePayload.getShipping().getPrice(), is(orderAggregate.getShipping().getPrice()));
    assertThat(orderAggregatePayload.getShipping().getStatus().name(), is(orderAggregate.getShipping().getStatus().name()));
    assertThat(orderAggregatePayload.getShipping().getCreatedAt(), is(orderAggregate.getShipping().getCreatedAt()));
  }

  @Test
  public void shouldGetErrorOrder() throws Exception {
    //Given
    final String id = "1";
    when(this.orderUseCase.getOrder(id)).thenReturn(Uni.createFrom().nullItem());

    //When
    final UniAssertSubscriber<OrderAggregatePayload> result =
        this.orderResource.getOrder(id).subscribe().withSubscriber(UniAssertSubscriber.create());

    //Then
    assertThat(result, is(notNullValue()));
    result.awaitFailure();
    result.assertFailedWith(NotFoundException.class);
  }

  @Test
  public void shouldCreatedOrder() throws Exception {
    //Given
    final OrderRequestPayload orderRequestPayload = OrderRequestPayload.builder().productId("1").userId("2").price(10L).build();
    final OrderAggregate orderAggregate = OrderAggregate.builder().build();
    final UriInfo uriInfo = mock(UriInfo.class);
    final UriBuilder uriBuilder = new UriBuilderImpl();
    final String id = "1";
    when(this.orderUseCase.create(any())).thenReturn(Uni.createFrom().item(orderAggregate.toBuilder().id(id).build()));
    when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

    //When
    final Uni<Response> result = this.orderResource.create(orderRequestPayload, uriInfo);

    //Then
    assertThat(result, is(notNullValue()));
    final Response response = result.subscribeAsCompletionStage().get();
    assertThat(response.getLocation(), is(notNullValue()));
    assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
    assertThat(response.getLocation(), is(notNullValue()));
    assertThat(response.getLocation(), is(new UriBuilderImpl().path(id).build()));
  }

  @Test
  public void shouldCreatedOrderError() throws Exception {
    //Given
    final OrderRequestPayload orderRequestPayload = OrderRequestPayload.builder().productId("1").userId("2").price(10L).build();
    final UriInfo uriInfo = mock(UriInfo.class);
    final UriBuilder uriBuilder = new UriBuilderImpl();
    when(this.orderUseCase.create(any())).thenReturn(Uni.createFrom().failure(RuntimeException::new));
    when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);

    //When
    final Uni<Response> result = this.orderResource.create(orderRequestPayload, uriInfo);

    //Then
    assertThat(result, is(notNullValue()));
    final Response response = result.subscribeAsCompletionStage().get();
    assertThat(response.getStatus(), is(Status.BAD_REQUEST.getStatusCode()));
  }

}