package com.blu.kafka.model

import java.io.Serializable

interface KafkaMessage {

    fun getId(): Serializable
}