package org.example.model.responses.dto

import model.enums.CurrencyPair

data class OrderCancelResponse (
    val orderId: String,
    val currencyPair: CurrencyPair
)