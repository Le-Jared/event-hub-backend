package com.fdmgroup.backend_eventhub.livechat.configuration;

import com.fdmgroup.backend_eventhub.livechat.constant.KafkaConstants;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {
  @Bean
  public Map<String, Object> consumerConfigurations() {
    Map<String, Object> configurations = new HashMap<>();

    configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKER);
    configurations.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID);
    configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configurations.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    return configurations;
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigurations());
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(2);
    return factory;
  }
}
