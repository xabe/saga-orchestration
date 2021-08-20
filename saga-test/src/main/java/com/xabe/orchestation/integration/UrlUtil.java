package com.xabe.orchestation.integration;

import static java.lang.String.format;

public class UrlUtil {

  private static final String SCHEMA_REGISTRY = "http://%s:%s/subjects/%s-value/versions";

  private static final String SCHEMA_REGISTRY_COMPATIBILITY = "http://%s:%s/config/%s-value";

  private static final UrlUtil INSTANCE = new UrlUtil();

  private final String urlSchemaRegistryOrder;

  private final String urlSchemaRegistryCompatibilityOrder;

  private final String urlSchemaRegistryStatus;

  private final String urlSchemaRegistryCompatibilityStatus;

  private UrlUtil() {
    final String registryHost = System.getProperty("schemaregistry.host", "localhost");
    final String registryPort = System.getProperty("schemaregistry.port", "8081");
    this.urlSchemaRegistryOrder = format(SCHEMA_REGISTRY, registryHost, registryPort, "orders.v1");
    this.urlSchemaRegistryStatus = format(SCHEMA_REGISTRY, registryHost, registryPort, "status.v1");
    this.urlSchemaRegistryCompatibilityOrder = format(SCHEMA_REGISTRY_COMPATIBILITY, registryHost, registryPort, "orders.v1");
    this.urlSchemaRegistryCompatibilityStatus = format(SCHEMA_REGISTRY_COMPATIBILITY, registryHost, registryPort, "status.v1");
  }

  public static UrlUtil getInstance() {
    return INSTANCE;
  }

  public String getSchemaRegistryOrder() {
    return this.urlSchemaRegistryOrder;
  }

  public String getUrlSchemaRegistryStatus() {
    return this.urlSchemaRegistryStatus;
  }

  public String getSchemaRegistryCompatibilityOrder() {
    return this.urlSchemaRegistryCompatibilityOrder;
  }

  public String getUrlSchemaRegistryCompatibilityStatus() {
    return this.urlSchemaRegistryCompatibilityStatus;
  }
}

