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
    var lastSuccessId: Int = 0

    open fun consume(messages: List<KafkaMessage>, acknowledgment: Acknowledgment) {
        logger.info("last Success Id: $lastSuccessId => next message: ${messages[0].getId()}")
        if((lastSuccessId + 1) != messages[0].getId().toString().toInt()) {
            logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        }
        val batchId = getBatchId()
        logReceivedMessages(batchId, messages)

        val futures = messages
            .map { message -> createJobIfNotProcessed(batchId, message) }
            .map { executorService.submit(it) }

        val doneStatus = mutableListOf<Boolean>()
        for (index in futures.indices) {
            val future = futures[index]
            try {
                future.get()
                processedMessages.add(messages[index].getId())
                doneStatus.add(true)
            } catch (e: Exception) {
                logFailedProcess(batchId, messages[index], index)
                doneStatus.add(false)
            }
        }

        val lastTrueIndex = (doneStatus.indexOf(false).takeIf { it != -1 } ?: doneStatus.size) - 1

        if (lastTrueIndex != -1) {
            acknowledgment.acknowledge(lastTrueIndex)
            lastSuccessId = messages[lastTrueIndex].getId().toString().toInt()
            logger.info("Send Ack for index: $lastTrueIndex")
        }

        if (lastTrueIndex != doneStatus.size - 1) {
            acknowledgment.nack(lastTrueIndex + 1, Duration.ZERO)
            logger.info("Send Nack for index: ${lastTrueIndex + 1}")
        }

    }

    protected open fun createJobIfNotProcessed(batchId: Int, message: KafkaMessage): Callable<Any> {
        val messageId = message.getId()
        if (processedMessages.contains(messageId))
            return Callable {
                LogUtil.log(
                    logger.atTrace(),
                    "Create empty callable for processed message",
                    "batchId",
                    batchId,
                    "message",
                    message
                )
            }


        return createJob(batchId, message)
    }

    abstract fun createJob(batchId: Int, message: KafkaMessage): Callable<Any>

    protected fun getBatchId(): Int = Random.nextInt(100, 1000)

    protected fun logFailedProcess(batchId: Int, message: KafkaMessage, messageIndex: Int) {
        LogUtil.log(
            logger.atError(),
            "Occur Exception in processing message $message", // TODO(Remove message from log message)
            "batchId", batchId,
            "index", messageIndex,
            "message", message
        )
    }

    protected fun logReceivedMessages(batchId: Int, messages: List<KafkaMessage>) {
        LogUtil.log(
            logger.atTrace(), "Received new Batch Messages",
            "batchId", batchId,
            "size", messages.size,
            "messages", messages
        )
    }
}