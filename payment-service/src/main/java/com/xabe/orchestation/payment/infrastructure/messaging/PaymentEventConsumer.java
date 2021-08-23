package com.xabe.orchestation.payment.infrastructure.messaging;

import com.xabe.orchestation.common.infrastructure.Event;
import com.xabe.orchestation.common.infrastructure.event.EventConsumer;
import com.xabe.orchestation.common.infrastructure.event.EventPublisher;
import com.xabe.orchestation.payment.domain.entity.Payment;
import com.xabe.orchestation.payment.domain.entity.PaymentStatus;
import com.xabe.orchestation.payment.domain.event.PaymentCreateCommandEvent;
import com.xabe.orchestation.payment.domain.repository.PaymentRepository;
import com.xabe.orchestation.payment.infrastructure.messaging.mapper.MessagingMapper;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

@ApplicationScoped
public class PaymentEventConsumer implements EventConsumer {

  private final Logger logger;

  private final PaymentRepository paymentRepository;

  private final MessagingMapper messagingMapper;

  private final EventPublisher eventPublisher;

  private final Map<Class, Consumer<Event>> mapHandlerEvent;

  @Inject
  public PaymentEventConsumer(final Logger logger, final PaymentRepository paymentRepository, final MessagingMapper messagingMapper,
      final EventPublisher eventPublisher) {
    this.logger = logger;
    this.paymentRepository = paymentRepository;
    this.messagingMapper = messagingMapper;
    this.eventPublisher = eventPublisher;
    this.mapHandlerEvent = Map.of(PaymentCreateCommandEvent.class, this::paymentCreateCommandEvent);
  }

  @Override
  public void consume(final Event event) {
    this.mapHandlerEvent.getOrDefault(event.getClass(), this::ignoreEvent).accept(event);
  }

  private void ignoreEvent(final Event event) {
    this.logger.warn("Ignore event {}", event);
  }

  private void paymentCreateCommandEvent(final Event event) {
    final PaymentCreateCommandEvent paymentCreateCommandEvent = PaymentCreateCommandEvent.class.cast(event);
    final Payment payment = this.messagingMapper.toEntity(paymentCreateCommandEvent);
    this.paymentRepository.create(payment).subscribe().with(this::sendOrderCreated, this.sendOrderNotCreated(payment));
  }

  private Consumer<Throwable> sendOrderNotCreated(final Payment payment) {
    return throwable -> {
      this.eventPublisher.tryPublish(this.messagingMapper.toEvent(payment.toBuilder().status(PaymentStatus.CANCELED).build()));
      this.logger.error("Error to save Order: {}", payment, throwable);
    };
  }

  private void sendOrderCreated(final Payment payment) {
    this.eventPublisher.tryPublish(this.messagingMapper.toEvent(payment));
  }
}
