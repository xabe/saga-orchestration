package com.xabe.orchestration.orchestrator.domain.repository;

import com.xabe.orchestation.common.infrastructure.repository.Repository;
import com.xabe.orchestration.orchestrator.domain.entity.OrderAggregate;

public interface OrderRepository extends Repository<OrderAggregate, String> {

}
