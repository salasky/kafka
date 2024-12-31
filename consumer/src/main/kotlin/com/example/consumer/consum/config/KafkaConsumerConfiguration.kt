package com.example.consumer.consum.config

import com.example.produce.dto.TaskKafkaDto
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer

/**
 * Настройки кафки Consumer
 */
@Configuration
class KafkaConsumerConfiguration(
    val kafkaProperties: KafkaProperties
) {

    /**
     * deserializer.setUseTypeHeaders(false) обязателен
     * Статьи хранят ДТО в других пакетах
     */
    @Bean
    fun taskConsumerFactory(): ConsumerFactory<String, TaskKafkaDto> {
        val deserializer = JsonDeserializer(TaskKafkaDto::class.java)
        //Если ваши сообщения не содержат заголовки типов, то десериализатор не сможет определить тип данных по умолчанию.
        // В этом случае вам нужно явно указать тип данных, используемый для десериализации.
        // Если вы знаете, что все сообщения в определенном топике будут одного типа,
        // вы можете упростить конфигурацию, отключив использование заголовков типов и явно указав тип данных.
        deserializer.setUseTypeHeaders(false)
        return listenerConsumerFactory(deserializer)
    }

    /**
     * Данный бин нужен для того, чтобы читать сообщения из кафки пачками, а не по одному
     */
    @Bean("taskConsumerContainerFactory")
    fun taskConsumerContainerFactory(
        consumerFactory: ConsumerFactory<String, TaskKafkaDto>
    ): ConcurrentKafkaListenerContainerFactory<String, TaskKafkaDto> {
        return listenerContainerFactory(consumerFactory)
    }

    private fun consumerConfig(): MutableMap<String, Any> {
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

    private fun <T> listenerConsumerFactory(deserializer: Deserializer<T>): ConsumerFactory<String, T> {
        return DefaultKafkaConsumerFactory(
            consumerConfig(),
            StringDeserializer(),
            deserializer
        )
    }

    private fun <T> listenerContainerFactory(
        consumerFactory: ConsumerFactory<String, T>
    ): ConcurrentKafkaListenerContainerFactory<String, T> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, T>()
        factory.consumerFactory = consumerFactory
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        return factory
    }

    @Bean
    @Qualifier("deserializationHandler")
    fun taskDeserializationExceptionHandler(): CommonErrorHandler {
        return DeserializationExceptionHandler()
    }
}