package com.blu.kafka.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.util.backoff.FixedBackOff


@EnableAsync
@Configuration
class KafkaConsumerConfig @Autowired constructor(
    private val consumerFactory: ConsumerFactory<String, String>
) {
//    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        factory.isBatchListener = false
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL

        val errorHandler = DefaultErrorHandler(FixedBackOff(1000L, 3L))
        errorHandler.isAckAfterHandle = true

        factory.setCommonErrorHandler(errorHandler)
        return factory
    }

    @Bean
    fun batchKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.isBatchListener = true
        factory.consumerFactory = consumerFactory
//        factory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH

        return factory
    }
}