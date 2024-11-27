package com.blu.kafka.service

import com.blu.kafka.exception.RetryableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

@Service
class WorkService {

    private var startTime: AtomicLong? = null
    private var endTime = AtomicLong(System.nanoTime())
    private var successCounter = AtomicLong(0)
    private var failureCounter = AtomicLong(0)

    private val latency = 500L
    private val successRatio = 90

    private val receivedMessageCounter: MutableMap<String, Int> = ConcurrentHashMap()
    private val successMessagesCounter: MutableMap<String, Int> = ConcurrentHashMap()
    private val failureMessagesCounter: MutableMap<String, Int> = ConcurrentHashMap()

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun doWork(batchId: Int, message: String) {
        startTime = startTime ?: AtomicLong(System.nanoTime())

        receivedMessageCounter[message] = receivedMessageCounter.getOrDefault(message, 0) + 1
        val latency = getRandomLatency().toLong()
        logger.info("processing message: {batchId: $batchId, message: '$message', Latency: $latency} in thread ${Thread.currentThread().name}")
        Thread.sleep(latency)

        endTime = AtomicLong(System.nanoTime())

        val random = Random.nextInt(0, 100)
        if (random > successRatio) {
            failureMessagesCounter[message] = failureMessagesCounter.getOrDefault(message, 0) + 1
            failureCounter.incrementAndGet()
            logger.error("Failed processing message: {batchId: $batchId, message: '$message'} in thread ${Thread.currentThread().name}")
            throw RetryableException(message)
        }

        successMessagesCounter[message] = successMessagesCounter.getOrDefault(message, 0) + 1
        successCounter.incrementAndGet()
    }

    fun logFinalResult() {
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<< Result >>>>>>>>>>>>>>>>>>>>")
        logger.info("Success: ${successCounter.get()}")
        logger.info("Failure: ${failureCounter.get()}")
        val totalReceived = receivedMessageCounter.values.sum()
        val totalSuccesses = successMessagesCounter.values.sum()
        val totalFailures = failureMessagesCounter.values.sum()
        logger.info("Total Received By Map: $totalReceived -> Max Received: ${receivedMessageCounter.maxBy { it.value }}")
        logger.info("Total success By Map: $totalSuccesses -> Max Success: ${successMessagesCounter.maxBy { it.value }}")
        logger.info("Total failures By Map: $totalFailures -> Max failures: ${failureMessagesCounter.maxBy { it.value }}")
        5
        logger.info("Total processed: ${successCounter.get() + failureCounter.get()}")
        logger.info("Expected ratio: $successRatio : Final Ratio: ${(successCounter.get() * 100) / (successCounter.get() + failureCounter.get())}")

        val elapsedTime = (endTime.get() - startTime!!.get()) / 1_000_000
        logger.info("Elapsed Time: $elapsedTime ms")

    }

    private fun getRandomLatency(): Int = 500 //(Random.nextInt(1000, 3000)/ 1000) * 1000
}