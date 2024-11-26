package com.blu.kafka.service

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import com.blu.kafka.model.KafkaMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.Callable

@Service
@KafkaListener(topics = ["$KAFKA_TOPIC"], groupId = "mf-kafka-group-id", containerFactory = "batchContainerFactory")
class NewKafkaConsumer @Autowired constructor(
    private val workService: WorkService
): KafkaBatchConsumer() {

    @KafkaHandler
    override fun consume(@Payload messages: List<KafkaMessage>, acknowledgment: Acknowledgment) {
        println("Received new Messages: ${messages.size} => ${messages.toTypedArray().contentToString()}")
        acknowledgment.acknowledge()
//        super.consume(messages, acknowledgment)
    }

    override fun createJob(batchId: Int, message: KafkaMessage): Callable<Any> {
        return Callable {
            workService.doWork(batchId, message.getId().toString())
        }
    }
}