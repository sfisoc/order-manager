package org.example.model.entities

import model.enums.CurrencyPair
import model.enums.OrderStatus
import model.enums.OrderSide
import model.enums.TimeInForce
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class Order (
    val id: String = UUID.randomUUID().toString(),
    val side: OrderSide,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: CurrencyPair,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val timeInForce: TimeInForce = TimeInForce.GTC,
    val status: OrderStatus = OrderStatus.ACTIVE,
    val customerOrderId: Int? = null,
    val orderCount: Int = 0
    )


//     "side": "sell",
//            "quantity": "0.00006",
//            "price": "1852331",
//            "currencyPair": "BTCZAR",
//            "orderCount": 1
