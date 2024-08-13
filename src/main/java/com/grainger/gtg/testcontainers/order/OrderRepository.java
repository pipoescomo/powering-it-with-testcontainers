package com.grainger.gtg.testcontainers.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<OrderModel, Long> {

  Optional<OrderModel> findByOrderNumber(String orderNumber);
}