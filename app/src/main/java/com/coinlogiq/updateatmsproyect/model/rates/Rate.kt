package com.coinlogiq.updateatmsproyect.model.rates

import java.util.*

data class Rate(
    val userId: String = "",
    val text: String = "",
    val rate: Float = 0f,
    val createdAt: Date = Date(),
    val profileImgUrl: String = "")