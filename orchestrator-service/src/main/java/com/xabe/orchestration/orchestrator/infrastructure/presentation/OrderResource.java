package com.xabe.orchestration.orchestrator.infrastructure.presentation;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.infrastructure.application.OrderUseCase;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.mapper.PresentationMapper;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderAggregatePayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderRequestPayload;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.function.Function;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;

@Singleton
@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class OrderResource {

  private final OrderUseCase orderUseCase;

  private final PresentationMapper orderPayloadMapper;

  @GET
  public Uni<List<OrderAggregatePayload>> getOrders() {
    return this.orderUseCase.getOrders().map(this.orderPayloadMapper::toPayloads);
  }

  @Path("/{id}")
  @GET
  public Uni<OrderAggregatePayload> getOrder(@PathParam("id") final String id) {
    return this.orderUseCase.getOrder(id).map(this.orderPayloadMapper::toPayload)
        .onItem().ifNull().failWith(NotFoundException::new);
  }

  @POST
  public Uni<Response> create(@Valid final OrderRequestPayload orderRequestPayload, @Context final UriInfo uriInfo) {
    return this.orderUseCase.create(this.orderPayloadMapper.toEntity(orderRequestPayload))
        .map(this.createResponseSuccessCreate(uriInfo))
        .onFailure().recoverWithItem(() -> Response.status(Response.Status.BAD_REQUEST).build());
  }

  private Function<OrderAggregate, Response> createResponseSuccessCreate(final UriInfo uriInfo) {
    return order -> Response.created(uriInfo.getRequestUriBuilder().path(order.getId()).build()).build();
  }

}
