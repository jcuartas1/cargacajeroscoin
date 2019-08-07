package com.coinlogiq.updateatmsproyect.model.messages

import java.util.*

data class Message(
    val authorId: String = "",
    val message: String = "",
    val profileImagUrl: String = "",
    val sentAt: Date = Date()) {
}