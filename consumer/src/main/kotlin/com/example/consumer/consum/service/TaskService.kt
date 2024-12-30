package com.example.consumer.consum.service

import com.example.produce.dto.TaskKafkaDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TaskService {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun printTask(task: TaskKafkaDto){
        println(task)
    }
}