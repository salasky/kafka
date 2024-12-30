package com.example.produce.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.kafka.producer")
class KafkaProperties {
    lateinit var taskTopicName: String
    lateinit var bootstrapServers: List<String>
    lateinit var clientId: String
    var lingerMS: Long = 200
}
