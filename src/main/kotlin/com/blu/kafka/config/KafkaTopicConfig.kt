package com.blu.kafka.config

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class KafkaTopicConfig {

    @Bean
    fun kafkaTopic(): NewTopic {
        return NewTopic(KAFKA_TOPIC, 1, 1.toShort())
    }

}