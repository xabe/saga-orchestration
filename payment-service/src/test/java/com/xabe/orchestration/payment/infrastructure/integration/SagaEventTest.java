package com.xabe.orchestration.payment.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.xabe.avro.v1.MessageEnvelopePayment;
import com.xabe.avro.v1.MessageEnvelopeStatus;
import com.xabe.avro.v1.Metadata;
import com.xabe.avro.v1.Payment;
import com.xabe.avro.v1.PaymentCancelCommand;
import com.xabe.avro.v1.PaymentCanceledEvent;
import com.xabe.avro.v1.PaymentCreateCommand;
import com.xabe.avro.v1.PaymentCreatedEvent;
import com.xabe.avro.v1.PaymentOperationStatus;
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

  private static KafkaProducer<MessageEnvelopePayment> KAFKA_PRODUCER;

  private Long paymentId;

  @BeforeAll
  public static void init() throws InterruptedException {
    Unirest.post(UrlUtil.getInstance().getSchemaRegistryPayment()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopePayment.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getSchemaRegistryCompatibilityPayment()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    Unirest.post(UrlUtil.getInstance().getUrlSchemaRegistryStatus()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("schema", MessageEnvelopeStatus.getClassSchema().toString())).asJson();
    Unirest.put(UrlUtil.getInstance().getUrlSchemaRegistryCompatibilityStatus())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(Map.of("compatibility", "Forward")).asJson();

    KAFKA_CONSUMER = new KafkaConsumer<>("status.v1", (message, payloadClass) -> message.getPayload().getClass().equals(payloadClass));
    KAFKA_PRODUCER = new KafkaProducer<>("payments.v1");
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
    final PaymentCreateCommand paymentCreateCommand =
        PaymentCreateCommand.newBuilder().setPurchaseId(purchaseId).setProductId("1").setUserId("2").setPrice(100L).setSentAt(Instant.now())
            .build();
    final MessageEnvelopePayment messageEnvelopePayment = MessageEnvelopePayment.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(paymentCreateCommand)
        .build();

    //When
    KAFKA_PRODUCER.send(messageEnvelopePayment, () -> purchaseId);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopeStatus> result = KAFKA_CONSUMER.expectMessagePipe(PaymentCreatedEvent.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final PaymentCreatedEvent paymentCreatedEvent = PaymentCreatedEvent.class.cast(result.getV2().getPayload());
          assertThat(paymentCreatedEvent.getUpdatedAt(), is(notNullValue()));
          assertThat(paymentCreatedEvent.getOperationStatus(), is(PaymentOperationStatus.SUCCESS));
          final Payment payment = paymentCreatedEvent.getPayment();
          assertThat(payment.getId(), is(notNullValue()));
          this.paymentId = payment.getId();
          assertThat(payment.getPurchaseId(), is(paymentCreateCommand.getPurchaseId()));
          assertThat(payment.getProductId(), is(paymentCreateCommand.getProductId()));
          assertThat(payment.getUserId(), is(paymentCreateCommand.getUserId()));
          assertThat(payment.getPrice(), is(paymentCreateCommand.getPrice()));
          assertThat(payment.getStatus().name(), is("ACCEPTED"));
          assertThat(payment.getCreatedAt(), is(notNullValue()));
          return true;
        });
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  public void shouldCancelOrderSaga() throws Exception {
    //Given
    final String purchaseId = "1111";
    final PaymentCancelCommand paymentCancelCommand =
        PaymentCancelCommand.newBuilder().setPaymentId(this.paymentId).setPurchaseId(purchaseId).setProductId("1").setUserId("2")
            .setSentAt(Instant.now())
            .build();
    final MessageEnvelopePayment messageEnvelopePayment = MessageEnvelopePayment.newBuilder()
        .setMetadata(this.createMetaData())
        .setPayload(paymentCancelCommand)
        .build();

    //When
    KAFKA_PRODUCER.send(messageEnvelopePayment, () -> purchaseId);

    //Then
    Awaitility.await().pollDelay(DELAY_MS, TimeUnit.MILLISECONDS).pollInterval(POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
        .atMost(TIMEOUT_MS, TimeUnit.MILLISECONDS).until(() -> {
          final Tuple2<String, MessageEnvelopeStatus> result = KAFKA_CONSUMER.expectMessagePipe(PaymentCanceledEvent.class, TIMEOUT_MS);
          assertThat(result, is(notNullValue()));
          assertThat(result.getV1(), is(notNullValue()));
          assertThat(result.getV2(), is(notNullValue()));
          final PaymentCanceledEvent paymentCanceledEvent = PaymentCanceledEvent.class.cast(result.getV2().getPayload());
          assertThat(paymentCanceledEvent.getUpdatedAt(), is(notNullValue()));
          assertThat(paymentCanceledEvent.getOperationStatus(), is(PaymentOperationStatus.SUCCESS));
          final Payment payment = paymentCanceledEvent.getPayment();
          assertThat(payment.getId(), is(notNullValue()));
          assertThat(payment.getPurchaseId(), is(paymentCancelCommand.getPurchaseId()));
          assertThat(payment.getProductId(), is(paymentCancelCommand.getProductId()));
          assertThat(payment.getUserId(), is(paymentCancelCommand.getUserId()));
          assertThat(payment.getPrice(), is(100L));
          assertThat(payment.getStatus().name(), is("CANCELED"));
          assertThat(payment.getCreatedAt(), is(notNullValue()));
          return true;
        });
  }

  protected Metadata createMetaData() {
    return Metadata.newBuilder().setDomain("payment").setName("payment").setAction("create").setVersion("vTest")
        .setTimestamp(DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())).build();
  }

}
