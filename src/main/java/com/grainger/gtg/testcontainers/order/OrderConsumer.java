package com.grainger.gtg.testcontainers.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderConsumer {

  private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

  private final OrderService orderService;

  @KafkaListener(topics = OrderConstants.ORDER_TOPIC, groupId = "update-order")
  public void consumeOrderUpdate(ConsumerRecord<String, String> orderRecord) {
    String orderEvent = orderRecord.value();
    logger.info( "Received order paid event: {}", orderEvent);

    try {
      var mapper = new ObjectMapper();
      OrderModel orderModel = mapper.readValue(orderEvent, OrderModel.class);

      if (orderModel.getStatus().equals("PAID")) {
        logger.info("Order paid: {}", orderModel);
        orderService.payOrder(orderModel.getOrderNumber());
      }

      if(orderModel.getStatus().equals("SHIPPED")) {
        logger.info("Order ready to ship: {}", orderModel);
        orderService.shipOrder(orderModel.getOrderNumber());
      }
    } catch (Exception e) {
      logger.error("Failed to process order paid event", e);
    }
  }
}
