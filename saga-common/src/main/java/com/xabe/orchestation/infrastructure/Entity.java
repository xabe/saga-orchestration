package com.xabe.orchestation.infrastructure;

import java.io.Serializable;

public interface Entity<T> extends Serializable {

  T getId();

}
