package com.grainger.gtg.testcontainers.order;

import java.util.Optional;

public interface OrderService {
  Optional<Order> getOrder(String orderNumber);
  Order createOrder(Order order);

  void payOrder(String orderNumber);
  void shipOrder(String orderNumber);
}
