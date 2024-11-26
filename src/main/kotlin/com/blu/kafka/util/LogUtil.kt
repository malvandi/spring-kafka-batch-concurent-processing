package com.blu.kafka.util

import org.slf4j.spi.LoggingEventBuilder

class LogUtil {
    companion object {
        fun init(builder: LoggingEventBuilder, message: String, arguments: Map<String, Any?>): LoggingEventBuilder {
            builder.setMessage(message)
            arguments.forEach { (key, value) -> builder.addKeyValue(key, value) }

            return builder
        }

        fun init(builder: LoggingEventBuilder, message: String, vararg arguments: Any?): LoggingEventBuilder {
            builder.setMessage(message)
            val mapArguments = mutableMapOf<String, Any?>()
            for (index in arguments.indices step 2) {
                mapArguments[arguments[index]?.toString() ?: "null"] =
                    if (arguments.size >= index + 2) arguments[index + 1] else null
            }

            return init(builder, message, mapArguments)
        }

        fun log(builder: LoggingEventBuilder, message: String, arguments: Map<String, Any?>) {
            init(builder, message, arguments).log()
        }

        fun log(builder: LoggingEventBuilder, message: String, vararg arguments: Any?) {
            init(builder, message, arguments).log()
        }
    }
}