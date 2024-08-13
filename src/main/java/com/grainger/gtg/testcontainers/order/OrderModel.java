package com.grainger.gtg.testcontainers.order;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "purchase_order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderModel {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private UUID id;
  @Column(unique = true)
  private String orderNumber;
  private String name;
  private String email;
  private String status;
}
