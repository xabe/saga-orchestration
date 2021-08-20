package com.xabe.orchestation.infrastructure.persistence;

import com.xabe.orchestation.infrastructure.persistence.dto.OrderDTO;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepositoryPanache implements PanacheRepositoryBase<OrderDTO, Long> {

}
