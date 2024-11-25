package com.blu.kafka.service

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import kotlin.random.Random


@Service
@KafkaListener(topics = ["$KAFKA_TOPIC"], groupId = "mf-kafka-group-id", containerFactory = "batchContainerFactory")
class KafkaConsumer @Autowired constructor(
    private val workService: WorkService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    //    @Async
    @KafkaHandler
    fun consume(@Payload message: String) {
        val batchId = Random.nextInt(10, 100)
        logger.info("Received new Messages: {batchId: $batchId, messages: $message}")
        workService.doWork(batchId, message)
    }
}