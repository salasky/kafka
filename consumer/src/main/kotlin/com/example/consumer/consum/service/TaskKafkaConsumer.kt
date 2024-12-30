package com.example.consumer.consum.service

import com.example.produce.dto.TaskKafkaDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class TaskKafkaConsumer(
    private val taskService: TaskService
) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["#{kafkaProperties.taskTopicName}"],
        containerFactory = "taskConsumerContainerFactory"
    )
    fun taskListener(@Payload messages: List<TaskKafkaDto>) {
        messages.forEach {
            logger.warn("KAFKA получила сообщение:  {}", it.orderNum)
            taskService.printTask(it)
        }
    }
}