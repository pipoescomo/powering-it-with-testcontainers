package com.grainger.gtg.testcontainers.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

  private final OrderRepository orderRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Optional<Order> getOrder(String orderNumber) {
    return orderRepository.findByOrderNumber(orderNumber)
        .map(OrderServiceImpl::buildOrder);
  }

  @Override
  public Order createOrder(Order order) {
    logger.info( "Order created: {}", order);
    OrderModel orderModel = buildOrderModel(order);
    orderRepository.save(orderModel);

    try {
      String message = mapper.writeValueAsString(orderModel);
      kafkaTemplate.send(OrderConstants.ORDER_TOPIC, message);
    } catch (JsonProcessingException e) {
      logger.error("Failed to publish order message", e);
    }

    return buildOrder(orderModel);
  }

  @Override
  public void payOrder(String orderNumber) {
    orderRepository.findByOrderNumber(orderNumber).ifPresent(orderModel -> {
      orderModel.setStatus("PAID");
      orderRepository.save(orderModel);
    });
  }

  @Override
  public void shipOrder(String orderNumber) {
    orderRepository.findByOrderNumber(orderNumber).ifPresent(orderModel -> {
      orderModel.setStatus("SHIPPED");
      orderRepository.save(orderModel);
    });
  }

  private static Order buildOrder(OrderModel orderModel) {
    return Order.builder()
        .id(orderModel.getId())
        .orderNumber(orderModel.getOrderNumber())
        .name(orderModel.getName())
        .status(orderModel.getStatus())
        .email(orderModel.getEmail())
        .build();
  }

  private static OrderModel buildOrderModel(Order order) {
    return OrderModel.builder()
      .orderNumber(order.orderNumber())
      .name(order.name())
      .email(order.email())
      .status(order.status())
      .build();
  }
}
