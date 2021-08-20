package com.xabe.orchestation.infrastructure.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.GsonBuilder;
import com.xabe.orchestation.infrastructure.presentation.payload.OrderPayload;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.gson.GsonObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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

  public static final String USER_ID = "1";

  public static final String PRODUCT_ID = "1";

  public static final String PURCHASE_ID = "1111";

  private final int serverPort = 8009;

  private String url;

  @BeforeAll
  public static void init() throws IOException {
    Unirest.config().setObjectMapper(new GsonObjectMapper(Converters.registerAll(new GsonBuilder()).create()));
  }

  @Test
  @Order(1)
  public void shouldCreateOrder() throws Exception {
    //Given
    final OrderPayload orderPayload = OrderPayload.builder().productId(PRODUCT_ID).userId(USER_ID).purchaseId(PURCHASE_ID).build();

    //When
    final HttpResponse<JsonNode> response = Unirest.post(String.format("http://localhost:%d/api/orders", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).body(orderPayload).asJson();

    //Then
    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(201));
    final List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
    assertThat(locations, is(notNullValue()));
    assertThat(locations, is(hasSize(1)));
    this.url = locations.get(0);
  }

  @Test
  @Order(2)
  public void shouldGetOrder() throws Exception {
    //Given

    //When
    final HttpResponse<OrderPayload> response = Unirest.get(this.url)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(OrderPayload.class);

    //Then
    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));
    assertThat(response.getBody(), is(notNullValue()));
    final OrderPayload orderPayload = response.getBody();
    assertThat(orderPayload.getId(), is(notNullValue()));
    assertThat(orderPayload.getPurchaseId(), is(PURCHASE_ID));
    assertThat(orderPayload.getProductId(), is(PRODUCT_ID));
    assertThat(orderPayload.getUserId(), is(USER_ID));
    assertThat(orderPayload.getPrice(), is(greaterThan(0L)));
    assertThat(orderPayload.getStatus().name(), is("CREATED"));
    assertThat(orderPayload.getCreatedAt(), is(notNullValue()));
  }

  @Test
  @Order(3)
  public void shouldGetOrders() {

    final HttpResponse<OrderPayload[]> response = Unirest.get(String.format("http://localhost:%d/api/orders", this.serverPort))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).asObject(OrderPayload[].class);

    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(200));
    assertThat(response.getBody().length, is(greaterThanOrEqualTo(1)));
  }

}
