package com.grainger.gtg.testcontainers;

import com.grainger.gtg.testcontainers.order.OrderConstants;
import com.grainger.gtg.testcontainers.order.OrderModel;
import com.grainger.gtg.testcontainers.order.OrderRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getRecords;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

  @LocalServerPort
  private Integer port;

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  ConsumerFactory<String, String> consumerFactory;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private OrderRepository orderRepository;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
  }

  @Test
  void contextLoads() {
    // context loads
  }

  @Test
  void createOrder_shouldReturnSavedOrderAndPublishToKafka() throws Exception {
    String request = """
      {
        "orderNumber": "123",
        "name": "frodo baggins",
        "email": "frodo@grainger.com",
        "status": "CREATED"
      }
      """;

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/orders")
        .then()
        .statusCode(201)
        .body("id", notNullValue())
        .body("orderNumber", equalTo("123"))
        .body("name", equalTo("frodo baggins"))
        .body("email", equalTo("frodo@grainger.com"))
        .body("status", equalTo("CREATED"));

    try (Consumer<String, String> consumerTest = consumerFactory.createConsumer("test", "test-id")) {
      consumerTest.subscribe(Collections.singleton("order"));
      ConsumerRecords<String, String> records = getRecords(consumerTest);
      ConsumerRecord<String, String> last = getMostRecentRecord(records);
      JSONObject actualJson = new JSONObject(last.value());
      JSONAssert.assertEquals(request, actualJson.toString(), false);
    }
  }

  @Nullable
  private static ConsumerRecord<String, String> getMostRecentRecord(ConsumerRecords<String, String> records) {
    Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
    ConsumerRecord<String, String> last=null;
    while(iterator.hasNext()){
      last = iterator.next();
    }
    return last;
  }

  @Test
  void getOrder_shouldReturnPaidOrder() throws IOException{
    String newOrder = """
      {
            "orderNumber": "1234",
            "name": "jose baggins",
            "email": "jose@grainger.com",
            "status": "CREATED"
          }
      """;
    OrderModel orderModel = objectMapper.readValue(newOrder, OrderModel.class);
    orderRepository.save(orderModel);

    // Simulate the order was paid
    String paidOrder = """
      {
            "orderNumber": "1234",
            "name": "jose baggins",
            "email": "jose@grainger.com",
            "status": "PAID"
          }
      """;
    kafkaTemplate.send(OrderConstants.ORDER_TOPIC, paidOrder);

    await()
        .pollInterval(Duration.ofSeconds(3))
        .atMost(10, SECONDS)
        .untilAsserted(() -> given()
            .contentType(ContentType.JSON)
            .when()
            .get("/orders/1234")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("orderNumber", equalTo("1234"))
            .body("name", equalTo("jose baggins"))
            .body("email", equalTo("jose@grainger.com"))
            .body("status", equalTo("PAID"))
        );
  }

  @Test
  void getOrder_shouldReturnShippedOrder() throws IOException {
    String newOrder = """
      {
            "orderNumber": "12345",
            "name": "emilio baggins",
            "email": "emilio@grainger.com",
            "status": "PAID"
          }
      """;
    OrderModel orderModel = objectMapper.readValue(newOrder, OrderModel.class);
    orderRepository.save(orderModel);

    // Simulate the order was shipped
    String shippedOrder = """
      {
            "orderNumber": "12345",
            "name": "emilio baggins",
            "email": "emilio@grainger.com",
            "status": "SHIPPED"
          }
      """;
    kafkaTemplate.send(OrderConstants.ORDER_TOPIC, shippedOrder);

    await()
        .pollInterval(Duration.ofSeconds(3))
        .atMost(10, SECONDS)
        .untilAsserted(() -> given()
            .contentType(ContentType.JSON)
            .when()
            .get("/orders/12345")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("orderNumber", equalTo("12345"))
            .body("name", equalTo("emilio baggins"))
            .body("email", equalTo("emilio@grainger.com"))
            .body("status", equalTo("SHIPPED")));
  }

}
