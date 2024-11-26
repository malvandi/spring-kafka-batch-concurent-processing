package com.blu.kafka.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.random.Random


@Service
//@KafkaListener(topics = ["$KAFKA_TOPIC"], groupId = "mf-kafka-group-id", containerFactory = "batchContainerFactory")
class KafkaConsumer @Autowired constructor(
    private val workService: WorkService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
//    @KafkaHandler
    fun consume(@Payload message: String) {
        val batchId = getBatchId()
        logger.info("Received new Messages: {batchId: $batchId, messages: $message}")
//        return workService.doFutureWork(batchId, message)
    }

//    @KafkaHandler
    fun consumeBatch(@Payload messages: List<String>, ack: Acknowledgment) {
        val batchId = getBatchId()
        logger.info(
            "Received new Messages: {batchId: $batchId, size: ${messages.size}, messages: ${
                messages.toTypedArray().contentToString()
            }}"
        )

        val jobs = messages.mapIndexed { index, message -> getJobCallable(batchId, message, index) }

        val executorService = Executors.newFixedThreadPool(5)
        val futures = jobs.map { executorService.submit(it) }

        for (index in futures.indices) {
            val future = futures[index]
            try {
                logger.info("Getting the value of ${messages[index]}")
                future.get()
                logger.info("Successfully send the ack ${messages[index]}")
                ack.acknowledge(index)
            } catch (e: Exception) {
                logger.info("Occur Exception for Index: $index")
                ack.nack(index, Duration.ZERO)
                break
            }
        }


//        executorService.submit()
//        for (index in messages.indices) {
//            try {
//                workService.doWork(batchId, messages[index])
//                logger.info("Send Ack for ${messages[index]}")
//
//                ack.acknowledge(index)
//            } catch (e: Exception) {
//                logger.error("Send Nack for ${messages[index]}")
//                ack.nack(index, Duration.ofMillis(2500))
//            }
//        }

        //workService.doFutureWork(batchId, message)
    }

    private fun getJobCallable(batchId: Int, message: String, jobIndex: Int): Callable<Int> {
        return Callable {
//            workService.doWork(batchId, message)
            jobIndex
        }
    }

    private fun me(): (Int, String) -> Int {
        return { batchId: Int, message: String ->
            println("BatchId: $batchId, Message: $message")
            batchId
        }
    }

    /*private fun processJob(batchId: Int, message: String, jobIndex: Int): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            // Job 1 logic
            try {
                workService.doWork(batchId, message)
                true
            } catch (exception: Exception) {
                false
            }
        }
    }*/

    private fun getBatchId(): Int = Random.nextInt(10, 100)
}