package com.coinlogiq.updateatmsproyect.model

import java.util.*

data class Rate(
    val text: String,
    val rate: Float,
    val createdAt: Date,
    val profileImgUrl: String = "")