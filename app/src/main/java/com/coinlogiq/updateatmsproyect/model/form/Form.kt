package com.coinlogiq.updateatmsproyect.model.form

import java.util.*

data class Form(
    val atmUbicacion: String = "",
    val atmDireccion: String = "",
    val atmDueno: String = "",
    val atmId: String = "",
    val atmLongitud: String = "",
    val atmLatitud: String = "",
    val creatDate: Date = Date(),
    val buy_btc_percent: String = "",
    val buy_dash_percent: String = "",
    val buy_ltc_percent: String = "",
    val buy_eth_percent: String = "",
    val sell_btc_percent: String ="",
    val sell_dash_percent: String = "",
    val sell_ltc_percent: String = "",
    val sell_eth_percent: String ="",
    val balance_coin: Float = 0f
)