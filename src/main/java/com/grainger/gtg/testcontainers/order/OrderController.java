package com.grainger.gtg.testcontainers.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/{orderNumber}")
  public Order get(@PathVariable String orderNumber) {
    return orderService.getOrder(orderNumber).orElseThrow();
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public Order create(@RequestBody Order order) {
    return orderService.createOrder(order);
  }

}
