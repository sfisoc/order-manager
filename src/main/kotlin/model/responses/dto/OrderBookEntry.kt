package org.example.model.responses.api

import model.enums.CurrencyPair
import model.enums.OrderSide
import java.math.BigDecimal

data class OrderBookEntry(
    val side: OrderSide,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyPair: CurrencyPair
)