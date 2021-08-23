package com.xabe.orchestation.payment.infrastructure.persistence;

import com.xabe.orchestation.payment.domain.entity.Payment;
import com.xabe.orchestation.payment.domain.repository.PaymentRepository;
import com.xabe.orchestation.payment.infrastructure.persistence.mapper.PersistenceMapper;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApplicationScoped
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

  private final Logger logger;

  private final PersistenceMapper persistenceMapper;

  private final PaymentRepositoryPanache paymentRepositoryPanache;

  @Override
  public Uni<Payment> getPayment(final Long id) {
    this.logger.debug("Get payment {}", id);
    return this.paymentRepositoryPanache.findById(id).map(this.persistenceMapper::toEntity);
  }

  @Override
  public Uni<List<Payment>> getPayments() {
    this.logger.debug("Get payments");
    return this.paymentRepositoryPanache.listAll().map(this.persistenceMapper::toEntities);
  }

  @Override
  public Uni<Payment> create(final Payment payment) {
    this.logger.debug("Create payment {}", payment);
    return this.paymentRepositoryPanache.persistAndFlush(this.persistenceMapper.toDTO(payment)).map(this.persistenceMapper::toEntity);
  }
}