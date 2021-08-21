package com.xabe.orchestation.common.infrastructure.repository;

import com.xabe.orchestation.common.infrastructure.Entity;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<T extends Entity<U>, U> {

  void save(final T instance);

  Optional<T> load(final U id);

  List<T> find(final Predicate<T> filter);

  void delete(final U id);

}
