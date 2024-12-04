package com.blu.kafka

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName


@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun kafkaContainer(): KafkaContainer {
        return KafkaContainer(DockerImageName.parse("apache/kafka:3.9.0"))
    }

//    @DynamicPropertySource
//    fun overrideProperties(registry: DynamicPropertyRegistry, kafkaContainer: KafkaContainer) {
//        registry.add("spring.kafka.bootstrap-servers", kafkaContainer.bootstrapServers)
//    }

}