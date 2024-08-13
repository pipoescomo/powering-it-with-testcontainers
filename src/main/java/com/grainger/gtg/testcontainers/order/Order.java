package com.grainger.gtg.testcontainers.order;

import lombok.Builder;
import java.util.UUID;

@Builder
public record Order(UUID id, String orderNumber, String name, String email, String status) {

}
