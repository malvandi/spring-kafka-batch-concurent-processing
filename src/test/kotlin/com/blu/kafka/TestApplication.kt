package com.blu.kafka

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<KafkaApplication>().with(TestcontainersConfiguration::class).run(*args)
}