package com.xabe.orchestation.infrastructure.event;

import com.xabe.orchestation.infrastructure.Event;

public interface EventConsumer {

  void consume(final Event event);

}
