package com.xabe.orchestration.orchestrator.infrastructure.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.repository.OrderRepository;
import com.xabe.orchestration.orchestrator.infrastructure.OrderMother;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderAggregateDTO;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.mapper.PersistenceMapperImpl;
import io.smallrye.mutiny.Uni;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class OderRepositoryImplTest {

  private OrderRepositoryPanache orderRepositoryPanache;

  private OrderRepository orderRepository;

  @BeforeEach
  public void setUp() throws Exception {
    final Logger logger = mock(Logger.class);
    this.orderRepositoryPanache = mock(OrderRepositoryPanache.class);
    this.orderRepository = new OderRepositoryImpl(logger, new PersistenceMapperImpl(), this.orderRepositoryPanache);
  }

  @Test
  public void shouldGetOrder() throws Exception {
    //Given
    final String id = "612cb1ac04e7df1b34068c21";
    final OrderAggregateDTO orderAggregateDTO = OrderMother.createOrderAggregateDTO();
    when(this.orderRepositoryPanache.findById(new ObjectId(id))).thenReturn(Uni.createFrom().item(orderAggregateDTO));

    //When
    final Uni<OrderAggregate> result = this.orderRepository.load(id);

    //Then
    assertThat(result, is(notNullValue()));
    final OrderAggregate orderAggregate = result.subscribeAsCompletionStage().get();
    assertThat(orderAggregate.getId(), is(orderAggregateDTO.getId().toString()));
    assertThat(orderAggregate.getStatus().name(), is(orderAggregateDTO.getStatus().name()));
    assertThat(orderAggregate.getCreatedAt().toInstant(), is(orderAggregateDTO.getCreatedAt()));
    assertThat(orderAggregate.getOrder().getUserId(), is(orderAggregateDTO.getOrder().getUserId()));
    assertThat(orderAggregate.getOrder().getProductId(), is(orderAggregateDTO.getOrder().getProductId()));
    assertThat(orderAggregate.getOrder().getPrice(), is(orderAggregateDTO.getOrder().getPrice()));
    assertThat(orderAggregate.getOrder().getStatus().name(), is(orderAggregateDTO.getOrder().getStatus().name()));
    assertThat(orderAggregate.getOrder().getCreatedAt().toInstant(), is(orderAggregateDTO.getOrder().getCreatedAt()));
    assertThat(orderAggregate.getPayment().getUserId(), is(orderAggregateDTO.getPayment().getUserId()));
    assertThat(orderAggregate.getPayment().getProductId(), is(orderAggregateDTO.getPayment().getProductId()));
    assertThat(orderAggregate.getPayment().getPrice(), is(orderAggregateDTO.getPayment().getPrice()));
    assertThat(orderAggregate.getPayment().getStatus().name(), is(orderAggregateDTO.getPayment().getStatus().name()));
    assertThat(orderAggregate.getPayment().getCreatedAt().toInstant(), is(orderAggregateDTO.getPayment().getCreatedAt()));
    assertThat(orderAggregate.getShipping().getUserId(), is(orderAggregateDTO.getShipping().getUserId()));
    assertThat(orderAggregate.getShipping().getProductId(), is(orderAggregateDTO.getShipping().getProductId()));
    assertThat(orderAggregate.getShipping().getPrice(), is(orderAggregateDTO.getShipping().getPrice()));
    assertThat(orderAggregate.getShipping().getStatus().name(), is(orderAggregateDTO.getShipping().getStatus().name()));
    assertThat(orderAggregate.getShipping().getCreatedAt().toInstant(), is(orderAggregateDTO.getShipping().getCreatedAt()));
  }

  @Test
  public void shouldGetAllOrders() throws Exception {
    //Given
    final OrderAggregateDTO orderAggregateDTO = OrderMother.createOrderAggregateDTO();
    when(this.orderRepositoryPanache.listAll()).thenReturn(Uni.createFrom().item(List.of(orderAggregateDTO)));

    //When
    final Uni<List<OrderAggregate>> result = this.orderRepository.getAll();

    //Then
    assertThat(result, is(notNullValue()));
    final List<OrderAggregate> orderAggregates = result.subscribeAsCompletionStage().get();
    assertThat(orderAggregates, is(notNullValue()));
    assertThat(orderAggregates, is(hasSize(1)));
    final OrderAggregate orderAggregate = orderAggregates.get(0);
    assertThat(orderAggregate.getId(), is(orderAggregateDTO.getId().toString()));
    assertThat(orderAggregate.getStatus().name(), is(orderAggregateDTO.getStatus().name()));
    assertThat(orderAggregate.getCreatedAt().toInstant(), is(orderAggregateDTO.getCreatedAt()));
    assertThat(orderAggregate.getOrder().getUserId(), is(orderAggregateDTO.getOrder().getUserId()));
    assertThat(orderAggregate.getOrder().getProductId(), is(orderAggregateDTO.getOrder().getProductId()));
    assertThat(orderAggregate.getOrder().getPrice(), is(orderAggregateDTO.getOrder().getPrice()));
    assertThat(orderAggregate.getOrder().getStatus().name(), is(orderAggregateDTO.getOrder().getStatus().name()));
    assertThat(orderAggregate.getOrder().getCreatedAt().toInstant(), is(orderAggregateDTO.getOrder().getCreatedAt()));
    assertThat(orderAggregate.getPayment().getUserId(), is(orderAggregateDTO.getPayment().getUserId()));
    assertThat(orderAggregate.getPayment().getProductId(), is(orderAggregateDTO.getPayment().getProductId()));
    assertThat(orderAggregate.getPayment().getPrice(), is(orderAggregateDTO.getPayment().getPrice()));
    assertThat(orderAggregate.getPayment().getStatus().name(), is(orderAggregateDTO.getPayment().getStatus().name()));
    assertThat(orderAggregate.getPayment().getCreatedAt().toInstant(), is(orderAggregateDTO.getPayment().getCreatedAt()));
    assertThat(orderAggregate.getShipping().getUserId(), is(orderAggregateDTO.getShipping().getUserId()));
    assertThat(orderAggregate.getShipping().getProductId(), is(orderAggregateDTO.getShipping().getProductId()));
    assertThat(orderAggregate.getShipping().getPrice(), is(orderAggregateDTO.getShipping().getPrice()));
    assertThat(orderAggregate.getShipping().getStatus().name(), is(orderAggregateDTO.getShipping().getStatus().name()));
    assertThat(orderAggregate.getShipping().getCreatedAt().toInstant(), is(orderAggregateDTO.getShipping().getCreatedAt()));
  }

  @Test
  public void shouldUpsertOrder() throws Exception {
    //Given
    final OrderAggregate orderAggregate = OrderMother.createOrderAggregate();
    final OrderAggregateDTO orderAggregateDTO = OrderMother.createOrderAggregateDTO();
    doAnswer(answer -> Uni.createFrom().item(answer.getArguments()[0])).when(this.orderRepositoryPanache)
        .persistOrUpdate(orderAggregateDTO);

    //When
    final Uni<OrderAggregate> result = this.orderRepository.save(orderAggregate);

    //Then
    assertThat(result, is(notNullValue()));
    final OrderAggregate orderResult = result.subscribeAsCompletionStage().get();
    assertThat(orderResult, is(orderAggregate));
  }
}