package com.example.produce.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean
    fun newTaskTopic(kafkaProperties: KafkaProperties): NewTopic {
        return TopicBuilder
            .name(kafkaProperties.taskTopicName)
            .partitions(1)
            .replicas(1)
            .build()
    }
}