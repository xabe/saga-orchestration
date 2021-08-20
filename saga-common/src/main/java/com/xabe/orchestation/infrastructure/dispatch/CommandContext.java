package com.xabe.orchestation.infrastructure.dispatch;

import com.xabe.orchestation.infrastructure.AggregateRoot;
import com.xabe.orchestation.infrastructure.event.EventPublisher;
import com.xabe.orchestation.infrastructure.repository.Repository;
import lombok.NonNull;

public class CommandContext<T extends AggregateRoot<U>, U> {

  private final Repository<T, U> repository;

  private final EventPublisher eventPublisher;

  public CommandContext(final Repository<T, U> repository, final EventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @NonNull
  public Repository<T, U> getRepository() {
    return this.repository;
  }

  public EventPublisher getEventPublisher() {
    return this.eventPublisher;
  }
}
