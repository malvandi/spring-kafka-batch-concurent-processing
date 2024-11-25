package com.blu.kafka.config

import com.blu.kafka.config.Constant.Companion.KAFKA_TOPIC
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin;


@Configuration
class KafkaTopicConfig @Autowired constructor(
    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private val bootstrapAddress: String
) {
    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(configs)
    }

    @Bean
    fun kafkaTopic(): NewTopic {
        return NewTopic(KAFKA_TOPIC, 1, 1.toShort())
    }

}