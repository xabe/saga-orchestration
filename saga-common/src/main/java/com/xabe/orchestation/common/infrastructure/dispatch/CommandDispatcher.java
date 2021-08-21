package com.xabe.orchestation.common.infrastructure.dispatch;

import com.xabe.orchestation.common.infrastructure.AggregateRoot;
import com.xabe.orchestation.common.infrastructure.Command;

public interface CommandDispatcher<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U> {

  void dispatch(final Command<C, T, U> command);

}
