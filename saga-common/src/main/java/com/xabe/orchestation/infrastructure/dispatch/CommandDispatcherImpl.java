package com.xabe.orchestation.infrastructure.dispatch;

import com.xabe.orchestation.infrastructure.AggregateRoot;
import com.xabe.orchestation.infrastructure.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandDispatcherImpl<C extends CommandContext<T, U>, T extends AggregateRoot<U>, U>
    implements CommandDispatcher<C, T, U> {

  private final Logger LOG = LoggerFactory.getLogger(CommandDispatcherImpl.class);

  private final C context;

  public CommandDispatcherImpl(final C context) {
    this.context = context;
  }

  public C getContext() {
    return this.context;
  }

  @Override
  public void dispatch(final Command<C, T, U> command) {
    command.execute(this.context);
  }
}
