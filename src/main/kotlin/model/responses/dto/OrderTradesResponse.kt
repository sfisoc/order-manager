package model.responses.dto;

import model.enums.CurrencyPair;

import java.math.BigDecimal;
import java.time.LocalDateTime;

data class OrderTradesResponse(
    val id: String,
    val price:BigDecimal,
    val quantity: BigDecimal,
    val currencyPair:CurrencyPair,
    val timestamp:LocalDateTime
)
