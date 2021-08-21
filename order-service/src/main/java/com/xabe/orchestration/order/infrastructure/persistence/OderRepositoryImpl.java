package com.xabe.orchestration.order.infrastructure.persistence;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.repository.OrderRepository;
import com.xabe.orchestration.order.infrastructure.persistence.mapper.PersistenceMapper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class OderRepositoryImpl implements OrderRepository {

  private final Logger logger;

  private final PersistenceMapper persistenceMapper;

  private final OrderRepositoryPanache orderRepositoryPanache;

  @Override
  public Uni<Order> getOrder(final Long id) {
    this.logger.debug("Get Order {}", id);
    return this.orderRepositoryPanache.findById(id).map(this.persistenceMapper::toEntity);
  }

  @Override
  public Uni<List<Order>> getOrders() {
    this.logger.debug("Get Orders");
    return this.orderRepositoryPanache.listAll().map(this.persistenceMapper::toOrdersEntity);
  }

  @Override
  public Uni<Order> create(final Order order) {
    this.logger.debug("Create Order {}", order);
    return this.orderRepositoryPanache.persistAndFlush(this.persistenceMapper.toDTO(order)).map(this.persistenceMapper::toEntity);
  }
}