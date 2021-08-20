package com.xabe.orchestation.infrastructure.persistence;

import com.xabe.orchestation.domain.entity.Order;
import com.xabe.orchestation.domain.repository.OrderRepository;
import com.xabe.orchestation.infrastructure.persistence.mapper.OrderDTOMaper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class OderRepositoryImpl implements OrderRepository {

  private final Logger logger;

  private final OrderDTOMaper orderDTOMaper;

  private final OrderRepositoryPanache orderRepositoryPanache;

  @Override
  public Uni<Order> getOrder(final Long id) {
    this.logger.debug("Get Order {}", id);
    return this.orderRepositoryPanache.findById(id).map(this.orderDTOMaper::toOrderEntity);
  }

  @Override
  public Uni<List<Order>> getOrders() {
    this.logger.debug("Get Orders");
    return this.orderRepositoryPanache.listAll().map(this.orderDTOMaper::toOrdersEntity);
  }

  @Override
  public Uni<Order> create(final Order order) {
    this.logger.debug("Create Order {}", order);
    return this.orderRepositoryPanache.persistAndFlush(this.orderDTOMaper.toOrderDTO(order)).map(this.orderDTOMaper::toOrderEntity);
  }
}