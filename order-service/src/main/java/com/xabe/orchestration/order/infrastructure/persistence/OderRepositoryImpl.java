package com.xabe.orchestration.order.infrastructure.persistence;

import com.xabe.orchestration.order.domain.entity.Order;
import com.xabe.orchestration.order.domain.repository.OrderRepository;
import com.xabe.orchestration.order.infrastructure.persistence.dto.OrderDTO;
import com.xabe.orchestration.order.infrastructure.persistence.mapper.PersistenceMapper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
class OderRepositoryImpl implements OrderRepository {

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

  @Override
  public Uni<Order> update(final Long id, final Order order) {
    return this.orderRepositoryPanache.findById(id).flatMap(this.updateOrder(id, this.persistenceMapper.toDTO(order)))
        .map(this.persistenceMapper::toEntity);
  }

  private Function<OrderDTO, Uni<? extends OrderDTO>> updateOrder(final Long id, final OrderDTO newOrderDTO) {
    return orderDTO -> {
      if (Objects.isNull(orderDTO)) {
        this.logger.debug("Update: create Order with id {} {}", id, newOrderDTO);
        return this.orderRepositoryPanache.persistAndFlush(newOrderDTO);
      } else {
        orderDTO.setPrice(newOrderDTO.getPrice());
        orderDTO.setStatus(newOrderDTO.getStatus());
        orderDTO.setProductId(newOrderDTO.getProductId());
        orderDTO.setPurchaseId(newOrderDTO.getPurchaseId());
        orderDTO.setUserId(newOrderDTO.getUserId());
        this.logger.debug("Update: update Order with id {} {}", id, orderDTO);
        return this.orderRepositoryPanache.persistAndFlush(orderDTO);
      }
    };
  }


}