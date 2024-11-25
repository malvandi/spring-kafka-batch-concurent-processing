package com.blu.kafka.service

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer @Autowired constructor(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun sendMessage(message: String) {
        kafkaTemplate.send(KAFKA_TOPIC, message)
    }

}