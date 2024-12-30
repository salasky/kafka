package com.example.produce

import com.example.produce.produces.TaskKafkaProducer
import com.example.produce.service.TaskGenerator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProduceApplication

fun main(args: Array<String>) {
    val runApplication = runApplication<ProduceApplication>(*args)
    val taskGenerator = runApplication.getBean(TaskGenerator::class.java)
    val taskKafkaProducer = runApplication.getBean(TaskKafkaProducer::class.java)

    taskGenerator.generateRandomTaskKafkaDtos(300).forEach {
        Thread.sleep(1000)
        taskKafkaProducer.send(it)
    }

}
