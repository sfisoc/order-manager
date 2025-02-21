package org.example.model.entities

import model.enums.CurrencyPair
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Trade(
    val id: String = UUID.randomUUID().toString(),
    val price: BigDecimal,
    val quantity: BigDecimal,
    val currencyPair: CurrencyPair,
    val takerOrderId: String,
    val makerOrderId: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    )