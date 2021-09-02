package com.xabe.orchestration.orchestrator.infrastructure.config;

import com.xabe.orchestration.orchestrator.infrastructure.persistence.OrderRepositoryPanache;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationLifeCycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLifeCycle.class);

  @Inject
  OrderRepositoryPanache orderRepositoryPanache;

  void onStart(@Observes final StartupEvent ev) {
    LOGGER.info("The application is starting with profile {}", ProfileManager.getActiveProfile());
    LOGGER.info("Delete total document in mongodb : {}", this.orderRepositoryPanache.deleteAll().await().indefinitely());
  }
}
