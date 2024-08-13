package com.grainger.gtg.testcontainers.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  OrderRepository orderRepository;

  @InjectMocks
  OrderServiceImpl orderService;

  @Test
  void getProfile_shouldReturnProfileFromRepository() {
    // given
    UUID id = UUID.randomUUID();
    OrderModel orderModel = OrderModel.builder()
        .id(id)
        .orderNumber("orderNumber")
        .name("name")
        .email("email")
        .build();
    when(orderRepository.findByOrderNumber(orderModel.getOrderNumber())).thenReturn(Optional.of(orderModel));

    // when
    Optional<Order> order = orderService.getOrder(orderModel.getOrderNumber());

    // then
    assertTrue(order.isPresent());
    assertEquals(id, order.get().id());
    assertEquals("name", order.get().name());
    assertEquals("email", order.get().email());
  }
}