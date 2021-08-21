package com.xabe.orchestation.payment.infrastructure.presentation;

import com.xabe.orchestation.payment.domain.entity.Payment;
import com.xabe.orchestation.payment.infrastructure.application.PaymentUseCase;
import com.xabe.orchestation.payment.infrastructure.presentation.mapper.PresentationMapper;
import com.xabe.orchestation.payment.infrastructure.presentation.payload.PaymentPayload;
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
@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class PaymentResource {

  private final PaymentUseCase paymentUseCase;

  private final PresentationMapper presentationMapper;

  @GET
  public Uni<List<PaymentPayload>> getPayments() {
    return this.paymentUseCase.getPayments().map(this.presentationMapper::toPayloads);
  }

  @Path("/{id}")
  @GET
  public Uni<PaymentPayload> getPayment(@PathParam("id") final Long id) {
    return this.paymentUseCase.getPayment(id).map(this.presentationMapper::toPayload)
        .onItem().ifNull().failWith(NotFoundException::new);
  }

  @POST
  public Uni<Response> create(@Valid final PaymentPayload orderPayload, @Context final UriInfo uriInfo) {
    return this.paymentUseCase.create(this.presentationMapper.toEntity(orderPayload))
        .map(this.createResponseSuccessCreate(uriInfo))
        .onFailure().recoverWithItem(() -> Response.status(Response.Status.BAD_REQUEST).build());
  }

  private Function<Payment, Response> createResponseSuccessCreate(final UriInfo uriInfo) {
    return payment -> Response.created(uriInfo.getRequestUriBuilder().path(payment.getId().toString()).build()).build();
  }

}
