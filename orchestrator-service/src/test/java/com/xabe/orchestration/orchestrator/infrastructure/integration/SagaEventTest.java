package com.xabe.orchestration.orchestrator.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.GsonBuilder;
import com.xabe.avro.v1.MessageEnvelopeOrder;
import com.xabe.avro.v1.MessageEnvelopePayment;
import com.xabe.avro.v1.MessageEnvelopeShipping;
import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.Order;
import com.xabe.avro.v1.OrderCreateCommand;
import com.xabe.avro.v1.OrderCreatedEvent;
import com.xabe.avro.v1.OrderOperationStatus;
import com.xabe.avro.v1.OrderStatus;
import com.xabe.avro.v1.Payment;
import com.xabe.avro.v1.PaymentCreateCommand;
import com.xabe.avro.v1.PaymentCreatedEvent;
import com.xabe.avro.v1.PaymentOperationStatus;
import com.xabe.avro.v1.PaymentStatus;
import com.xabe.avro.v1.Shipping;
import com.xabe.avro.v1.ShippingCreateCommand;
import com.xabe.avro.v1.ShippingCreatedEvent;
import com.xabe.avro.v1.ShippingOperationStatus;
import com.xabe.avro.v1.ShippingStatus;
import com.xabe.orchestation.integration.KafkaConsumer;
import com.xabe.orchestation.integration.KafkaProducer;
import com.xabe.orchestation.integration.UrlUtil;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderAggregatePayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderAggregateStatusPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderRequestPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.OrderStatusPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.PaymentPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.PaymentStatusPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.ShippingPayload;
import com.xabe.orchestration.orchestrator.infrastructure.presentation.payload.ShippingStatusPayload;
import groovy.lang.Tuple2;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
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

  public static final int TIMEOUT_MS = 10000;

  public static final int DELAY_MS = 1500;

  public static final int POLL_INTERVAL_MS = 500;

  public static final String USER_ID = "1";

  public static final String PRODUCT_ID = "1";

  public static final Long PRICE = 100L;

  public static final Long ID_ORDER = 200L;

  public static final Long ID_PAYMENT = 400L;

  public static final Long ID_SHIPPING = 600L;

  private final int serverPort = 8000;

  private String id;

  private String url;

  private static KafkaProducer<MessageEnvelopeStatus> KAFKA_PRODUCER_STATUS;

  private static KafkaConsumer<MessageEnvelopeOrder> KAFKA_CONSUMER_ORDER;

  private static KafkaConsumer<MessageEnvelopePayment> KAFKA_CONSUMER_PAYMENT;

  private static KafkaConsumer<MessageEnvelopeShipping> KAFKA_CONSUMER_SHIPPING;

  @BeforeAll
  public static void init() throws InterruptedException {
    Unirest.config().setObjectMapper(new GsonObjectMapper(Converters.registerAll(new GsonBuilder()).create()));

    Unirest.post(UrlUtil.getInstance().getUrlSchemaRegistryStatus()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeStatus.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getUrlSchemaRegistryCompatibilityStatus())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    Unirest.post(UrlUtil.getInstance().getSchemaRegistryOrder()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeOrder.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityOrder()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    Unirest.post(UrlUtil.getInstance().getSchemaRegistryPayment()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopePayment.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityPayment()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    Unirest.post(UrlUtil.getInstance().getUrlSchemaRegistryShipping()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeShipping.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getUrlSchemaRegistryCompatibilityShipping())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    KAFKA_PRODUCER_STATUS = new KafkaProducer<>("status.v1");
    KAFKA_CONSUMER_ORDER =
        new KafkaConsumer<>("orders.v1", (message, payloadClass) -> message.getPayload().getClass().equals(payloadClass));
    KAFKA_CONSUMER_PAYMENT =
        new KafkaConsumer<>("payments.v1", (message, payloadClass) -> message.getPayload().getClass().equals(payloadClass));
    KAFKA_CONSUMER_SHIPPING =
        new KafkaConsumer<>("shipments.v1", (message, payloadClass) -> message.getPayload().getClass().equals(payloadClass));
    TimeUnit.SECONDS.sleep(5);
  }

  @AfterAll
  public static void end() {
    KAFKA_PRODUCER_STATUS.close();
    KAFKA_CONSUMER_ORDER.close();
    KAFKA_CONSUMER_PAYMENT.close();
    KAFKA_CONSUMER_SHIPPING.close();
  }

  @BeforeEach
  public void before() {
    KAFKA_CONSUMER_ORDER.before();
    KAFKA_CONSUMER_PAYMENT.before();
    KAFKA_CONSUMER_SHIPPING.before();
  }

  @Test
  @org.junit.jupiter.api.Order(1)
  public void shouldCreateOrderCommandSaga() throws Exception {
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
          final Tuple2<String, MessageEnvelopeOrder> result = KAFKA_CONSUMER_ORDER.expectMessagePipe(OrderCreateCommand.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final OrderCreateCommand orderCreateCommand = OrderCreateCommand.class.cast(result.getV2().getPayload());
          assertThat(orderCreateCommand.getPurchaseId(), is(notNullValue()));
          this.id = orderCreateCommand.getPurchaseId();
          assertThat(orderCreateCommand.getPrice(), is(PRICE));
          assertThat(orderCreateCommand.getUserId(), is(USER_ID));
          assertThat(orderCreateCommand.getProductId(), is(PRODUCT_ID));
          assertThat(orderCreateCommand.getSentAt(), is(notNullValue()));
          return true;
        });
    this.assertOrderAggregateStatus(OrderAggregateStatusPayload.ORDER_CREATED, this.validateStatus());
  }

  private Predicate<OrderAggregatePayload> validateStatus() {
    return orderAggregatePayload -> {
      assertThat(orderAggregatePayload.getOrder().getStatus().name(), is("UNKNOWN"));
      assertThat(orderAggregatePayload.getPayment().getStatus().name(), is("UNKNOWN"));
      assertThat(orderAggregatePayload.getShipping().getStatus().name(), is("UNKNOWN"));
      return true;
    };
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  public void shouldCreatePaymentCommandSaga() throws Exception {
    //Given
    final Order order = Order.newBuilder()
        .setId(ID_ORDER)
        .setPurchaseId(this.id)
        .setUserId(USER_ID)
        .setProductId(PRODUCT_ID)
        .setStatus(OrderStatus.CREATED)
        .setPrice(PRICE)
        .setCreatedAt(Instant.now())
        .build();
    final OrderCreatedEvent orderCreatedEvent =
        OrderCreatedEvent.newBuilder()
            .setOrder(order)
            .setOperationStatus(OrderOperationStatus.SUCCESS)
            .setUpdatedAt(Instant.now())
            .build();
    final MessageEnvelopeStatus messageEnvelopeStatus = MessageEnvelopeStatus.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(orderCreatedEvent)
        .build();

    //When
    KAFKA_PRODUCER_STATUS.send(messageEnvelopeStatus, () -> this.id);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopePayment> result =
              KAFKA_CONSUMER_PAYMENT.expectMessagePipe(PaymentCreateCommand.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final PaymentCreateCommand paymentCreateCommand = PaymentCreateCommand.class.cast(result.getV2().getPayload());
          assertThat(paymentCreateCommand.getPurchaseId(), is(notNullValue()));
          this.id = paymentCreateCommand.getPurchaseId();
          assertThat(paymentCreateCommand.getPrice(), is(PRICE));
          assertThat(paymentCreateCommand.getUserId(), is(USER_ID));
          assertThat(paymentCreateCommand.getProductId(), is(PRODUCT_ID));
          assertThat(paymentCreateCommand.getSentAt(), is(notNullValue()));
          return true;
        });
    this.assertOrderAggregateStatus(OrderAggregateStatusPayload.PAYMENT_PROCESSED, this::validateOrder);
  }

  private boolean validateOrder(final OrderAggregatePayload orderAggregatePayload) {
    final OrderPayload order = orderAggregatePayload.getOrder();
    assertThat(order, is(notNullValue()));
    assertThat(order.getId(), is(ID_ORDER));
    assertThat(order.getStatus(), is(OrderStatusPayload.CREATED));
    assertThat(order.getPrice(), is(PRICE));
    assertThat(order.getUserId(), is(USER_ID));
    assertThat(order.getProductId(), is(PRODUCT_ID));
    return true;
  }

  @Test
  @org.junit.jupiter.api.Order(3)
  public void shouldCreateShippingCommandSaga() throws Exception {
    //Given
    final Payment payment = Payment.newBuilder()
        .setId(ID_PAYMENT)
        .setPurchaseId(this.id)
        .setUserId(USER_ID)
        .setProductId(PRODUCT_ID)
        .setStatus(PaymentStatus.ACCEPTED)
        .setPrice(PRICE)
        .setCreatedAt(Instant.now())
        .build();
    final PaymentCreatedEvent paymentCreatedEvent =
        PaymentCreatedEvent.newBuilder()
            .setPayment(payment)
            .setOperationStatus(PaymentOperationStatus.SUCCESS)
            .setUpdatedAt(Instant.now())
            .build();
    final MessageEnvelopeStatus messageEnvelopeStatus = MessageEnvelopeStatus.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(paymentCreatedEvent)
        .build();

    //When
    KAFKA_PRODUCER_STATUS.send(messageEnvelopeStatus, () -> this.id);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopeShipping> result =
              KAFKA_CONSUMER_SHIPPING.expectMessagePipe(ShippingCreateCommand.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final ShippingCreateCommand shippingCreateCommand = ShippingCreateCommand.class.cast(result.getV2().getPayload());
          assertThat(shippingCreateCommand.getPurchaseId(), is(notNullValue()));
          this.id = shippingCreateCommand.getPurchaseId();
          assertThat(shippingCreateCommand.getPrice(), is(PRICE));
          assertThat(shippingCreateCommand.getUserId(), is(USER_ID));
          assertThat(shippingCreateCommand.getProductId(), is(PRODUCT_ID));
          assertThat(shippingCreateCommand.getSentAt(), is(notNullValue()));
          return true;
        });
    this.assertOrderAggregateStatus(OrderAggregateStatusPayload.SHIPPING_SENT, this::validatePayment);
  }

  private boolean validatePayment(final OrderAggregatePayload orderAggregatePayload) {
    final PaymentPayload payment = orderAggregatePayload.getPayment();
    assertThat(payment, is(notNullValue()));
    assertThat(payment.getId(), is(ID_PAYMENT));
    assertThat(payment.getStatus(), is(PaymentStatusPayload.ACCEPTED));
    assertThat(payment.getPrice(), is(PRICE));
    assertThat(payment.getUserId(), is(USER_ID));
    assertThat(payment.getProductId(), is(PRODUCT_ID));
    return true;
  }

  @Test
  @org.junit.jupiter.api.Order(4)
  public void shouldCreateSaga() throws Exception {
    //Given
    final Shipping shipping = Shipping.newBuilder()
        .setId(ID_SHIPPING)
        .setPurchaseId(this.id)
        .setUserId(USER_ID)
        .setProductId(PRODUCT_ID)
        .setStatus(ShippingStatus.ACCEPTED)
        .setPrice(PRICE)
        .setCreatedAt(Instant.now())
        .build();
    final ShippingCreatedEvent shippingCreatedEvent =
        ShippingCreatedEvent.newBuilder()
            .setShipping(shipping)
            .setOperationStatus(ShippingOperationStatus.SUCCESS)
            .setUpdatedAt(Instant.now())
            .build();
    final MessageEnvelopeStatus messageEnvelopeStatus = MessageEnvelopeStatus.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(shippingCreatedEvent)
        .build();

    //When
    KAFKA_PRODUCER_STATUS.send(messageEnvelopeStatus, () -> this.id);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          this.assertOrderAggregateStatus(OrderAggregateStatusPayload.SUCCESS, this::validateShipping);
          return true;
        });

  }

  private boolean validateShipping(final OrderAggregatePayload orderAggregatePayload) {
    final ShippingPayload shipping = orderAggregatePayload.getShipping();
    assertThat(shipping, is(notNullValue()));
    assertThat(shipping.getId(), is(ID_SHIPPING));
    assertThat(shipping.getStatus(), is(ShippingStatusPayload.ACCEPTED));
    assertThat(shipping.getPrice(), is(PRICE));
    assertThat(shipping.getUserId(), is(USER_ID));
    assertThat(shipping.getProductId(), is(PRODUCT_ID));
    return this.validateOrder(orderAggregatePayload) && this.validatePayment(orderAggregatePayload);
  }

  private void assertOrderAggregateStatus(final OrderAggregateStatusPayload status, final Predicate<OrderAggregatePayload> predicate) {
    final HttpResponse<OrderAggregatePayload> response = Unirest.get(this.url)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(OrderAggregatePayload.class);

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));
    assertThat(response.getBody(), is(notNullValue()));
    final OrderAggregatePayload orderAggregatePayload = response.getBody();
    assertThat(orderAggregatePayload.getId(), is(this.id));
    assertThat(orderAggregatePayload.getStatus(), is(status));
    assertThat(predicate.test(orderAggregatePayload), is(true));
  }

  protected Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("order").setName("order").setAction("create").setVersion("vTest")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

}
