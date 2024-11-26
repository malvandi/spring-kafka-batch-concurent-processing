package com.blu.kafka.service

import com.blu.kafka.model.KafkaMessage
import com.blu.kafka.util.LogUtil
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import java.io.Serializable
import java.time.Duration
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

abstract class KafkaBatchConsumer(
    protected val executorService: ExecutorService = Executors.newFixedThreadPool(5)
) {

    protected val processedMessages = Collections.synchronizedSet(mutableSetOf<Serializable>())

    private val logger = LoggerFactory.getLogger(this::class.java)

    open fun consume(messages: List<KafkaMessage>, acknowledgment: Acknowledgment) {
        val batchId = getBatchId()
        LogUtil.log(
            logger.atTrace(), "Received new Batch Messages",
            "batchId", batchId,
            "size", messages.size,
            "messages", messages
        )

        val futures = messages
            .map { message -> createJobIfNotProcessed(batchId, message) }
            .map { executorService.submit(it) }

        var exception: Exception? = null
        for (index in futures.indices) {
            val future = futures[index]
            try {
                future.get()
                processedMessages.add(messages[index].getId())
                acknowledgment.acknowledge(index)
            } catch (e: Exception) {
                LogUtil.log(logger.atError(), "Occur Exception in processing message", "index", index, "message", messages[index])
                acknowledgment.nack(index, Duration.ZERO)
                if(exception == null)
                    exception
            }
        }
    }

    fun createJobIfNotProcessed(batchId: Int, message: KafkaMessage): Callable<Any> {
        val messageId = message.getId()
        if(processedMessages.contains(messageId))
            return Callable {
                LogUtil.log(logger.atTrace(), "Create empty callable for processed message", "batchId", batchId, "message", message)
            }


        return createJob(batchId, message)
    }

    abstract fun createJob(batchId: Int, message: KafkaMessage): Callable<Any>

    protected fun getBatchId(): Int = Random.nextInt(100, 1000)
}