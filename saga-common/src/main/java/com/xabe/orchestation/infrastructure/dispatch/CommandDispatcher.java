package com.xabe.orchestation.infrastructure.dispatch;

import com.xabe.orchestation.infrastructure.AggregateRoot;
import com.xabe.orchestation.infrastructure.Command;

public interface CommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {
  
  void dispatch(final Command<C, T, U> command);

}
