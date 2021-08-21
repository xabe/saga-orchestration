package com.xabe.orchestration.order.infrastructure.presentation;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.infrastructure.application.OrderUseCase;
import com.xabe.orchestration.order.infrastructure.presentation.mapper.PresentationMapper;
import com.xabe.orchestration.order.infrastructure.presentation.payload.OrderPayload;
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
  public Uni<List<OrderPayload>> getOrders() {
    return this.orderUseCase.getOrders().map(this.orderPayloadMapper::toPayloads);
  }

  @Path("/{id}")
  @GET
  public Uni<OrderPayload> getOrder(@PathParam("id") final Long id) {
    return this.orderUseCase.getOrder(id).map(this.orderPayloadMapper::toPayload)
        .onItem().ifNull().failWith(NotFoundException::new);
  }

  @POST
  public Uni<Response> create(@Valid final OrderPayload orderPayload, @Context final UriInfo uriInfo) {
    return this.orderUseCase.create(this.orderPayloadMapper.toEntity(orderPayload))
        .map(this.createResponseSuccessCreate(uriInfo))
        .onFailure().recoverWithItem(() -> Response.status(Response.Status.BAD_REQUEST).build());
  }

  private Function<Order, Response> createResponseSuccessCreate(final UriInfo uriInfo) {
    return order -> Response.created(uriInfo.getRequestUriBuilder().path(order.getId().toString()).build()).build();
  }

}
