package com.xabe.orchestration.order.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.avro.v1.MessageEnvelopeOrder;
import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.Order;
import com.xabe.avro.v1.OrderCancelCommand;
import com.xabe.avro.v1.OrderCanceledEvent;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.avro.v1.OrderCreatedEvent;
import com.xabe.avro.v1.OrderOperationStatus;
import com.xabe.orchestation.integration.KafkaConsumer;
import com.xabe.orchestation.integration.KafkaProducer;
import com.xabe.orchestation.integration.UrlUtil;
import groovy.lang.Tuple2;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import kong.unirest.Unirest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SagaEventTest {

  public static final int TIMEOUT_MS = 5000;

  public static final int DELAY_MS = 1500;

  public static final int POLL_INTERVAL_MS = 500;

  private static KafkaConsumer<MessageEnvelopeStatus> KAFKA_CONSUMER;

  private static KafkaProducer<MessageEnvelopeOrder> KAFKA_PRODUCER;

  private Long orderId;

  @BeforeAll
  public static void init() throws InterruptedException {
    Unirest.post(UrlUtil.getInstance().getSchemaRegistryOrder()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeOrder.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityOrder()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    Unirest.post(UrlUtil.getInstance().getUrlSchemaRegistryStatus()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeStatus.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getUrlSchemaRegistryCompatibilityStatus())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    KAFKA_CONSUMER = new KafkaConsumer<>("status.v1", (message, payloadClass) -> message.getPayload().getClass().equals(payloadClass));
    KAFKA_PRODUCER = new KafkaProducer<>("orders.v1");
    TimeUnit.SECONDS.sleep(5);
  }

  @AfterAll
  public static void end() {
    KAFKA_PRODUCER.close();
    KAFKA_CONSUMER.close();
  }

  @BeforeEach
  public void before() {
    KAFKA_CONSUMER.before();
  }

  @Test
  @org.junit.jupiter.api.Order(1)
  public void shouldCreateOrderSaga() throws Exception {
    //Given
    final String purchaseId = "1111";
    final OrderCreateCommand orderCreateCommand =
        OrderCreateCommand.newBuilder().setPurchaseId(purchaseId).setProductId("1").setUserId("2").setPrice(100L).setSentAt(Instant.now())
            .build();
    final MessageEnvelopeOrder messageEnvelopeOrder = MessageEnvelopeOrder.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(orderCreateCommand)
        .build();

    //When
    KAFKA_PRODUCER.send(messageEnvelopeOrder, () -> purchaseId);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopeStatus> result = KAFKA_CONSUMER.expectMessagePipe(OrderCreatedEvent.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.class.cast(result.getV2().getPayload());
          assertThat(orderCreatedEvent.getUpdatedAt(), is(notNullValue()));
          assertThat(orderCreatedEvent.getOperationStatus(), is(OrderOperationStatus.SUCCESS));
          final Order order = orderCreatedEvent.getOrder();
          assertThat(order.getId(), is(notNullValue()));
          this.orderId = order.getId();
          assertThat(order.getPurchaseId(), is(orderCreateCommand.getPurchaseId()));
          assertThat(order.getProductId(), is(orderCreateCommand.getProductId()));
          assertThat(order.getUserId(), is(orderCreateCommand.getUserId()));
          assertThat(order.getPrice(), is(orderCreateCommand.getPrice()));
          assertThat(order.getStatus().name(), is("CREATED"));
          assertThat(order.getCreatedAt(), is(notNullValue()));
          return true;
        });
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  public void shouldCancelOrderSaga() throws Exception {
    //Given
    final String purchaseId = "1111";
    final OrderCancelCommand orderCancelCommand =
        OrderCancelCommand.newBuilder().setOrderId(this.orderId).setPurchaseId(purchaseId).setProductId("1").setUserId("2")
            .setSentAt(Instant.now())
            .build();
    final MessageEnvelopeOrder messageEnvelopeOrder = MessageEnvelopeOrder.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(orderCancelCommand)
        .build();

    //When
    KAFKA_PRODUCER.send(messageEnvelopeOrder, () -> purchaseId);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopeStatus> result = KAFKA_CONSUMER.expectMessagePipe(OrderCanceledEvent.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final OrderCanceledEvent orderCanceledEvent = OrderCanceledEvent.class.cast(result.getV2().getPayload());
          assertThat(orderCanceledEvent.getUpdatedAt(), is(notNullValue()));
          assertThat(orderCanceledEvent.getOperationStatus(), is(OrderOperationStatus.SUCCESS));
          final Order order = orderCanceledEvent.getOrder();
          assertThat(order.getId(), is(notNullValue()));
          assertThat(order.getPurchaseId(), is(orderCancelCommand.getPurchaseId()));
          assertThat(order.getProductId(), is(orderCancelCommand.getProductId()));
          assertThat(order.getUserId(), is(orderCancelCommand.getUserId()));
          assertThat(order.getPrice(), is(100L));
          assertThat(order.getStatus().name(), is("CANCELED"));
          assertThat(order.getCreatedAt(), is(notNullValue()));
          return true;
        });
  }

  protected Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("order").setName("order").setAction("create").setVersion("vTest")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

}
