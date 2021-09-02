package com.xabe.orchestration.orchestrator.infrastructure.config;

import com.xabe.orchestation.common.infrastructure.dispatch.CommandDispatcher;
import com.xabe.orchestation.common.infrastructure.dispatch.CommandDispatcherImpl;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestration.orchestrator.domain.command.order.OrderCreateCommandContext;
import com.xabe.orchestration.orchestrator.domain.command.payment.PaymentCreateCommandContext;
import com.xabe.orchestration.orchestrator.domain.command.shipping.ShippingCreateCommandContext;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.repository.OrderRepository;
import io.quarkus.arc.DefaultBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@ApplicationScoped
public class AppConfiguration {

  @Produces
  @DefaultBean
  public OrderCreateCommandContext orderCreateCommandContext(final OrderRepository orderRepository,
      final @Named("OrderEventPublisher") EventPublisher eventPublisher) {
    return new OrderCreateCommandContext(orderRepository, eventPublisher);
  }

  @Produces
  @DefaultBean
  public PaymentCreateCommandContext paymentCreateCommandContext(final OrderRepository orderRepository,
      final @Named("PaymentEventPublisher") EventPublisher eventPublisher) {
    return new PaymentCreateCommandContext(orderRepository, eventPublisher);
  }

  @Produces
  @DefaultBean
  public ShippingCreateCommandContext shippingCreateCommandContext(final OrderRepository orderRepository,
      final @Named("ShippingEventPublisher") EventPublisher eventPublisher) {
    return new ShippingCreateCommandContext(orderRepository, eventPublisher);
  }

  @Produces
  @DefaultBean
  public CommandDispatcher<OrderCreateCommandContext, OrderAggregate, String> orderCommandDispatcher(
      final OrderCreateCommandContext context) {
    return new CommandDispatcherImpl<>(context);
  }

  @Produces
  @DefaultBean
  public CommandDispatcher<PaymentCreateCommandContext, OrderAggregate, String> paymentCommandDispatcher(
      final PaymentCreateCommandContext context) {
    return new CommandDispatcherImpl<>(context);
  }

  @Produces
  @DefaultBean
  public CommandDispatcher<ShippingCreateCommandContext, OrderAggregate, String> shippingCommandDispatcher(
      final ShippingCreateCommandContext context) {
    return new CommandDispatcherImpl<>(context);
  }

}
