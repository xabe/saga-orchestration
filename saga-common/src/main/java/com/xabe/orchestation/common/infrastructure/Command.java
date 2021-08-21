package com.xabe.orchestation.common.infrastructure;

import com.xabe.orchestation.common.infrastructure.dispatch.CommandContext;

public interface Command<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {

  U getAggregateRootId();

  void execute(final C context);
}
