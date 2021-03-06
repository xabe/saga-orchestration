package com.xabe.orchestration.orchestrator.infrastructure.persistence;

import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;
import com.xabe.orchestration.orchestrator.domain.repository.OrderRepository;
import com.xabe.orchestration.orchestrator.infrastructure.persistence.mapper.PersistenceMapper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
class OderRepositoryImpl implements OrderRepository {

  private final Logger logger;

  private final PersistenceMapper persistenceMapper;

  private final OrderRepositoryPanache orderRepositoryPanache;

  @Override
  public Uni<OrderAggregate> load(final String id) {
    this.logger.debug("Get Order {}", id);
    try {
      return this.orderRepositoryPanache.findById(new ObjectId(id)).map(this.persistenceMapper::toEntity);
    } catch (final IllegalArgumentException exception) {
      return Uni.createFrom().nullItem();
    }

  }

  @Override
  public Uni<List<OrderAggregate>> getAll() {
    this.logger.debug("Get Orders");
    return this.orderRepositoryPanache.listAll().map(this.persistenceMapper::toOrdersEntity);
  }

  @Override
  public Uni<OrderAggregate> save(final OrderAggregate orderAggregate) {
    this.logger.debug("Create Order {}", orderAggregate);
    return this.orderRepositoryPanache.persistOrUpdate(this.persistenceMapper.toDTO(orderAggregate)).map(this.persistenceMapper::toEntity);
  }

}