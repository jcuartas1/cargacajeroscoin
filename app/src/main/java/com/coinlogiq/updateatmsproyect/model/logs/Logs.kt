package com.coinlogiq.updateatmsproyect.model.logs

import java.util.*

data class Logs(
    val userId: String = "",
    val operacion: String = "",
    val atmId: String ="",
    val observacion: String ="",
    val creatLogDate: Date = Date()
)