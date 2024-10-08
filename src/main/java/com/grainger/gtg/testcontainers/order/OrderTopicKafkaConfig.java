package com.grainger.gtg.testcontainers.order;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class OrderTopicKafkaConfig {

  @Bean
  public NewTopic topic() {
    return TopicBuilder.name("order")
      .partitions(3)
      .replicas(1)
      .build();
  }

}
