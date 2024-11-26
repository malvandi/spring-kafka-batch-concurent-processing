package com.blu.kafka.model

import java.io.Serializable

class HelloMessage(
    var id: Long = 0,
    var message: String = ""
): KafkaMessage {
    override fun getId(): Serializable {
        return id
    }

    override fun toString(): String {
        return "{id: $id, message: $message}"
    }
}