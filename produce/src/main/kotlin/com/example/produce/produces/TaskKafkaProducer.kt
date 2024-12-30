package com.example.produce.produces

import com.example.produce.produces.util.KafkaEventProvider
import com.example.produce.produces.util.KafkaProducer
import com.example.produce.config.KafkaProperties
import com.example.produce.dto.TaskKafkaDto
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.stereotype.Component

@Component
class TaskKafkaProducer(
    val kafkaEventProvider: KafkaEventProvider,
    val kafkaProperties: KafkaProperties
): KafkaProducer<TaskKafkaDto> {

    override fun send(record: TaskKafkaDto, headers: List<RecordHeader>) {
        kafkaEventProvider.proceed(
            queue = kafkaProperties.taskTopicName,
            key = record.requestUuid,
            value = record,
            headers = headers
        )
    }
}