package com.example.produce.produces.util

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaEventProvider @Autowired constructor(
    private val kafkaTemplateStringKey: KafkaTemplate<String, Any>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Отправляет сообщение в Kafka топик.
     *
     * @param queue Имя топика.
     * @param key Ключ сообщения.
     * @param value Значение сообщения.
     * @param headers Заголовки сообщения.
     */
    fun proceed(queue: String, key: String?, value: Any, headers: List<RecordHeader>) {
        val record = ProducerRecord(queue, key, value)
        headers.forEach { record.headers().add(it) }

        val future = kafkaTemplateStringKey.send(record)

        future.whenComplete { sendResult, throwable ->
            if (throwable != null) {
                logger.error("Could not proceed message $key:$value with cause: ${throwable.cause} and message: ${throwable.message}")
            } else {
                logger.info("Message $key:$value sent successfully to topic $queue")
            }
        }
    }
}