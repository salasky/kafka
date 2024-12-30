package com.example.consumer.consum.config

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.errors.RecordDeserializationException
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.MessageListenerContainer

class DeserializationExceptionHandler(): CommonErrorHandler {

    private val logger = LoggerFactory.getLogger(DeserializationExceptionHandler::class.java)

    override fun handleOne(
        thrownException: Exception,
        record: ConsumerRecord<*, *>,
        consumer: Consumer<*, *>,
        container: MessageListenerContainer
    ): Boolean {
        handle(thrownException, consumer)
        return true
    }

    override fun handleOtherException(
        thrownException: java.lang.Exception,
        consumer: Consumer<*, *>,
        container: MessageListenerContainer,
        batchListener: Boolean
    ) {
        handle(thrownException, consumer)
    }

    private fun handle(
        exception: Exception,
        consumer: Consumer<*, *>
    ) {
        if (exception is RecordDeserializationException) {
            logger.error("Handle thrown exception in topic: {}, offset: {}", exception.topicPartition(), exception.offset(), exception)
            consumer.seek(exception.topicPartition(), exception.offset() + 1L)
            consumer.commitSync()
        } else {
            logger.error("Exception not handled. DeserializationExceptionHandler can't handle this type of exception", exception)
        }
    }
}