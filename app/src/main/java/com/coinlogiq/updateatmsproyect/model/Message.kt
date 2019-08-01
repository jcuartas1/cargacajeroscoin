package com.coinlogiq.updateatmsproyect.model

import java.util.*

data class Message(
    val authorId: String = "",
    val message: String = "",
    val profileImagUrl: String = "",
    val sentAt: Date = Date()) {
}