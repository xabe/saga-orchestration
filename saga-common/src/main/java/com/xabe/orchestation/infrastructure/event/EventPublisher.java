package com.xabe.orchestation.infrastructure.event;

import com.xabe.orchestation.infrastructure.Event;

public interface EventPublisher {

  void tryPublish(Event event);

}
