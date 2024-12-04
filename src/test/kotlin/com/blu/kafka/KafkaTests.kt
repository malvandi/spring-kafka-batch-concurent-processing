package com.blu.kafka

import com.blu.kafka.model.HelloMessage
import com.blu.kafka.service.KafkaProducer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class KafkaTests @Autowired constructor(
	private val kafkaProducer: KafkaProducer
) {

	@Test
	fun contextLoads() {
	}

	@Test
	fun sendMessage() {
		val message = "Hello World!"
		val helloMessage = HelloMessage(1, message)

		kafkaProducer.sendMessage(helloMessage)
	}
}
