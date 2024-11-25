package com.blu.kafka.service

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import kotlin.random.Random


@Service
class KafkaConsumer @Autowired constructor(
    private val workService: WorkService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    //    @Async
    @KafkaListener(topics = ["$KAFKA_TOPIC"], groupId = "mf-kafka-group-id")
    fun consume(@Payload message: List<String>) {

        val batchId = Random.nextInt(10, 100)
        logger.info("Received new Messages: {batchId: $batchId, messages: ${message.size}}")
        Thread.sleep(1000)
    }

//    @KafkaListener(topics = ["$KAFKA_TOPIC"], groupId = "mf-kafka-group-id", autoStartup = "true", concurrency = "5")
    fun batchConsume(@Payload messages: List<String>) {
        val batchId = Random.nextInt(0, 100)
        logger.info(
            "Received new batch Messages : {batchId: $batchId, countMessages: ${messages.size}, messages: ${
                messages.toTypedArray().contentToString()
            }}"
        )
        Thread.sleep(3000)
//        Thread.sleep(1000)
//        for (i in messages.indices) {
//            val random = Random.nextInt(0, 100)
//            if (random > 30) {
////                acknowledgment.acknowledge(i)
//                logger.info("Acknowledge in Batch: $batchId, messages: ${messages[i]} and index: $i")
//            } else {
////                acknowledgment.nack(i, Duration.ZERO)
//                logger.error("Nack in Batch: $batchId, messages: ${messages[i]} and index: $i")
//                throw BatchListenerFailedException("Nack in Batch: $batchId, messages: ${messages[i]} and index: $i", i)
//            }
////            println("i: $i")
////            acknowledgment.acknowledge(i)
//        }
//        Thread.sleep(1000)
        logger.info("Function Ended for batchId: $batchId")
    }
}