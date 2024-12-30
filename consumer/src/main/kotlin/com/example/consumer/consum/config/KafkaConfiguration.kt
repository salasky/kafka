package com.example.consumer.consum.config

import com.example.produce.dto.TaskKafkaDto
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.support.JacksonUtils
import org.springframework.kafka.support.serializer.JsonDeserializer

/**
 * Настройки кафки Consumer
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

    fun taskConsumerConfig(): MutableMap<String, Any> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = kafkaProperties.groupId
        configProps[ConsumerConfig.CLIENT_ID_CONFIG] = kafkaProperties.clientId
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        //В проде это значение больше
        configProps[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 3
        //Если один из Consumer-ов умер, а другие не ждали бесконечно
        configProps[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 3_000
        return configProps
    }

    @Bean
    fun taskConsumerFactory(objectMapper: ObjectMapper): ConsumerFactory<String, TaskKafkaDto> {
        val deserializer = JsonDeserializer(TaskKafkaDto::class.java)
        deserializer.setUseTypeHeaders(false)
        return DefaultKafkaConsumerFactory(
            taskConsumerConfig(),
            StringDeserializer(),
            deserializer
        )
    }

    @Bean
    @Qualifier("deserializationHandler")
    fun taskDeserializationExceptionHandler(): CommonErrorHandler {
        return DeserializationExceptionHandler()
    }


    /**
     * Данный бин нужен для того, чтобы читать сообщения из кафки пачками, а не по одному
     */
    @Bean("taskConsumerContainerFactory")
    fun taskConsumerContainerFactory(taskConsumerFactory: ConsumerFactory<String, TaskKafkaDto>
    ): ConcurrentKafkaListenerContainerFactory<String, TaskKafkaDto> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, TaskKafkaDto>()
        factory.isBatchListener =true
        factory.consumerFactory = taskConsumerFactory
        return factory
    }
}