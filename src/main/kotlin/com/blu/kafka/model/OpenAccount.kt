package com.blu.kafka.model

import java.util.*

class OpenAccount(
    val userDetail: UserDetail = UserDetail(),
    val eventDetail: EventDetail = EventDetail()
) {

    class UserDetail(
        val fieldType: String = "username",
        val fieldValue: String = ""
    )


    class EventDetail(
        val key: String = "OPEN_ACCOUNT",

        val timestamp: Long = Date().time,

        val inquiryResult: String? = null, // Optional field

        val message: String? = null // Optional field
    ) {
    }
}