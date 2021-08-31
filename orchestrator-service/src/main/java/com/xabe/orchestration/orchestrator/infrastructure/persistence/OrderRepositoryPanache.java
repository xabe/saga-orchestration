package com.xabe.orchestration.orchestrator.infrastructure.persistence;

import com.xabe.orchestration.orchestrator.infrastructure.persistence.dto.OrderAggregateDTO;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepositoryPanache implements ReactivePanacheMongoRepository<OrderAggregateDTO> {

}
