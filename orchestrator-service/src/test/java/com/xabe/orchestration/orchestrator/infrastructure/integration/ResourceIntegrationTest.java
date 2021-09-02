package com.xabe.orchestration.orchestrator.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.GsonBuilder;
import com.xabe.avro.v1.MessageEnvelopeOrder;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.orchestation.integration.KafkaConsumer;
import com.xabe.orchestation.integration.UrlUtil;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderAggregatePayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderRequestPayload;
import groovy.lang.Tuple2;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.gson.GsonObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class ResourceIntegrationTest {

  public static final int TIMEOUT_MS = 10000;

  public static final int DELAY_MS = 1500;

  public static final int POLL_INTERVAL_MS = 500;

  public static final String USER_ID = "1";

  public static final String PRODUCT_ID = "1";

  public static final Long PRICE = 100L;

  private final int serverPort = 8000;

  private String url;

  private static KafkaConsumer<MessageEnvelopeOrder> KAFKA_CONSUMER;

  @BeforeAll
  public static void init() throws IOException {
    Unirest.config().setObjectMapper(new GsonObjectMapper(Converters.registerAll(new GsonBuilder()).create()));

    Unirest.post(UrlUtil.getInstance().getSchemaRegistryOrder()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeOrder.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityOrder()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    KAFKA_CONSUMER = new KafkaConsumer<>("orders.v1", (message, payloadClass) -> message.getPayload().getClass().equals(payloadClass));
  }

  @AfterAll
  public static void end() {
    KAFKA_CONSUMER.close();
  }

  @BeforeEach
  public void before() {
    KAFKA_CONSUMER.before();
  }

  @Test
  @Order(1)
  public void shouldCreateOrder() throws Exception {
    //Given
    final OrderRequestPayload orderRequestPayload =
        OrderRequestPayload.builder().productId(PRODUCT_ID).userId(USER_ID).price(PRICE).build();

    //When
    final HttpResponse<JsonNode> response = Unirest.post(String.format("http://localhost:%d/api/orders", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).body(orderRequestPayload).asJson();

    //Then
    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(201));
    final List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
    assertThat(locations, is(notNullValue()));
    assertThat(locations, is(hasSize(1)));
    this.url = locations.get(0);

    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopeOrder> result = KAFKA_CONSUMER.expectMessagePipe(OrderCreateCommand.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final OrderCreateCommand orderCreateCommand = OrderCreateCommand.class.cast(result.getV2().getPayload());
          assertThat(orderCreateCommand.getPurchaseId(), is(notNullValue()));
          assertThat(orderCreateCommand.getPrice(), is(PRICE));
          assertThat(orderCreateCommand.getUserId(), is(USER_ID));
          assertThat(orderCreateCommand.getProductId(), is(PRODUCT_ID));
          assertThat(orderCreateCommand.getSentAt(), is(notNullValue()));
          return true;
        });
  }

  @Test
  @Order(2)
  public void shouldGetOrder() throws Exception {
    //Given

    //When
    final HttpResponse<OrderAggregatePayload> response = Unirest.get(this.url)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(OrderAggregatePayload.class);

    //Then
    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));
    assertThat(response.getBody(), is(notNullValue()));
    final OrderAggregatePayload orderAggregatePayload = response.getBody();
    assertThat(orderAggregatePayload.getId(), is(notNullValue()));
    assertThat(orderAggregatePayload.getOrder().getProductId(), is(PRODUCT_ID));
    assertThat(orderAggregatePayload.getOrder().getUserId(), is(USER_ID));
    assertThat(orderAggregatePayload.getOrder().getPrice(), is(PRICE));
    assertThat(orderAggregatePayload.getStatus(), is(notNullValue()));
    assertThat(orderAggregatePayload.getCreatedAt(), is(notNullValue()));
  }

  @Test
  @Order(3)
  public void shouldGetOrders() {

    final HttpResponse<OrderAggregatePayload[]> response = Unirest.get(String.format("http://localhost:%d/api/orders", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(OrderAggregatePayload[].class);

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));
    assertThat(response.getBody().length, is(greaterThanOrEqualTo(1)));
  }

}
