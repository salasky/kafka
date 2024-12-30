package com.example.produce.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.JacksonUtils
import org.springframework.kafka.support.serializer.JsonSerializer

/**
 * Настройки кафки
 */
@Configuration
class KafkaConfiguration(
    val kafkaProperties: KafkaProperties
) {
    /**
     * Хорошей практикой является использование ObjectMapper, который используется в целом для проекта
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        return JacksonUtils.enhancedObjectMapper()
    }

    /**
     * Ключи в кафке могут быть разных типов, в данном случае бин для String
     */
    @Bean
    fun kafkaTemplateStringKey(mapper: ObjectMapper): KafkaTemplate<String, Any> {
        val config: MutableMap<String, Any> = HashMap()
        //адрес кафки
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        config[ProducerConfig.CLIENT_ID_CONFIG] = kafkaProperties.clientId
        //Загружаем сообщения пачками, чтобы не грузить сеть
        config[ProducerConfig.LINGER_MS_CONFIG] = kafkaProperties.lingerMS
        //сериализатор ключей, может быть long и т.д.
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        val kafkaProducerFactory = DefaultKafkaProducerFactory<String, Any>(config)
        //добавляем свой objectMapper
        kafkaProducerFactory.valueSerializer = JsonSerializer(mapper)
        return KafkaTemplate(kafkaProducerFactory)
    }
}