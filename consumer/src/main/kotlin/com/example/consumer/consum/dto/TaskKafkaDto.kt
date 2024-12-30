package com.example.produce.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class TaskKafkaDto(
    @JsonProperty("orderNum")
    val orderNum: Long,
    @JsonProperty("content")
    val content: String,
    @JsonProperty("authorEmail")
    val authorEmail: String,
    @JsonProperty("requestUuid")
    val requestUuid: String = UUID.randomUUID().toString()
)