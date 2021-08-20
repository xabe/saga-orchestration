package com.xabe.orchestation.infrastructure.event;

import org.apache.avro.specific.SpecificRecord;

public interface EventHandler<T extends SpecificRecord> {

  void handle(T paylooad);

}