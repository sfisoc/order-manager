package org.example.model.entities

import model.enums.CurrencyPair
import model.enums.OrderSide
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

// "price": "43064",
//    "quantity": "0.00079928",
//    "currencyPair": "BTCUSDC",
//    "tradedAt": "2024-02-05T07:47:04.625Z",
//    "takerSide": "sell",
//    "sequenceId": 1203970033324130300,
//    "id": "c13c5166-c3fa-11ee-b1a8-c700095e5df0",
//    "quoteVolume": "34.42019392"