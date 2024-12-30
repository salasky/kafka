package com.example.produce.service

import com.example.produce.dto.TaskKafkaDto
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

@Service
class TaskGenerator {
    val order = AtomicLong(0)
    /**
     * Генератор случайных задач
     */
    fun generateRandomTaskKafkaDto(): TaskKafkaDto {
        val content = "Task-" + generateRandomString(10, 100)
        val authorEmail = "${generateRandomString(5, 10)}@rt.ru"
        return TaskKafkaDto(
            orderNum = order.getAndIncrement(),
            content = content,
            authorEmail = authorEmail
        )
    }

    private fun generateRandomString(minLength: Int, maxLength: Int): String {
        val length = Random.nextInt(minLength, maxLength + 1)
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun generateRandomTaskKafkaDtos(count: Int): List<TaskKafkaDto> {
        return List(count) { generateRandomTaskKafkaDto() }
    }
}