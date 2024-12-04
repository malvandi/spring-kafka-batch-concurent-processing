package com.blu.kafka.service

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import com.blu.kafka.model.KafkaMessage
import com.blu.kafka.model.OpenAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer @Autowired constructor(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun sendMessage(message: KafkaMessage) {
        kafkaTemplate.send(KAFKA_TOPIC, message)
    }

    fun sendMessage(message: OpenAccount) {
        kafkaTemplate.send(KAFKA_TOPIC, message)
    }

}