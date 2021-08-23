package com.xabe.orchestation.shipping.infrastructure.persistence;

import com.xabe.orchestation.shipping.domain.entity.Shipping;
import com.xabe.orchestation.shipping.domain.repository.ShippingRepository;
import com.xabe.orchestation.shipping.infrastructure.persistence.mapper.PersistenceMapper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class ShippingRepositoryImpl implements ShippingRepository {

  private final Logger logger;

  private final PersistenceMapper persistenceMapper;

  private final ShippingRepositoryPanache shippingRepositoryPanache;

  @Override
  public Uni<Shipping> getShipping(final Long id) {
    this.logger.debug("Get shipping {}", id);
    return this.shippingRepositoryPanache.findById(id).map(this.persistenceMapper::toEntity);
  }

  @Override
  public Uni<List<Shipping>> getShipments() {
    this.logger.debug("Get shipments");
    return this.shippingRepositoryPanache.listAll().map(this.persistenceMapper::toEntities);
  }

  @Override
  public Uni<Shipping> create(final Shipping shipping) {
    this.logger.debug("Create shipping {}", shipping);
    return this.shippingRepositoryPanache.persistAndFlush(this.persistenceMapper.toDTO(shipping)).map(this.persistenceMapper::toEntity);
  }
}