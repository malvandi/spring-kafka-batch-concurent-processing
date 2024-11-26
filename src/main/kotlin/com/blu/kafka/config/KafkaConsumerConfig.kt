package com.blu.kafka.config

import com.blu.kafka.exception.RetryableException
import com.blu.kafka.model.KafkaMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.util.backoff.BackOff
import org.springframework.util.backoff.FixedBackOff


@EnableAsync
@Configuration
class KafkaConsumerConfig @Autowired constructor(
    private val consumerFactory: ConsumerFactory<String, KafkaMessage>
) {

    /*@Bean
    fun consumerFactory(): ConsumerFactory<String, KafkaMessage> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "mf-kafka-group-id",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(
            configProps,
            StringDeserializer(),
            JsonDeserializer(KafkaMessage::class.java)
        )
    }*/

    @Bean
    fun batchContainerFactory(
        errorHandler: DefaultErrorHandler
    ): ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, KafkaMessage>()
        factory.isBatchListener = true
        factory.consumerFactory = consumerFactory

        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE

        factory.setCommonErrorHandler(errorHandler)
        return factory
    }

    @Bean
    fun errorHandler(): DefaultErrorHandler {
        val fixedBackOff: BackOff = FixedBackOff(1500, 2)
        val errorHandler = DefaultErrorHandler({ _, exception ->
            println("**************************************************")
            exception.printStackTrace()
        }, fixedBackOff)

        errorHandler.addRetryableExceptions(RetryableException::class.java)
        errorHandler.addNotRetryableExceptions(NullPointerException::class.java)

        return errorHandler
    }
}