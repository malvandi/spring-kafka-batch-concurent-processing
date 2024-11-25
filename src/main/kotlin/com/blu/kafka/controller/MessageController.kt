package com.blu.kafka.controller

import com.blu.kafka.service.KafkaProducer
import com.blu.kafka.service.WorkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
class MessageController @Autowired constructor(
    private val producer: KafkaProducer,
    private val workService: WorkService
) {

    var counter = 0

    @PostMapping("/kafka-message")
    fun sendMessage(@RequestParam(required = false) count: Int = 100) {
        for (i in 1..count)
            producer.sendMessage("SBM [${++counter}]")
    }

    @PostMapping("/kafka-result")
    fun logResult() {
        workService.logFinalResult()
    }

    @GetMapping("/test")
    fun test(): String? {
        val completableFuture = CompletableFuture.supplyAsync {"salam bar mahdi"}
        completableFuture.thenRun { println("Salam Bar Mahdi")}
        println("Running: ${completableFuture.get()}")
        return completableFuture.get()
    }
}