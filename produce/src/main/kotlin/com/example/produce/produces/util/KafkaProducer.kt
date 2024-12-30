package com.example.produce.produces.util

import org.apache.kafka.common.header.internals.RecordHeader

interface KafkaProducer<T> {
    fun send(record: T, headers: List<RecordHeader> = listOf())
}