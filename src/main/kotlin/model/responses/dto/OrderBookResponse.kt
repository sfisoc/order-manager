package model.responses.dto

import org.example.model.responses.internal.OrderBookEntry

data class OrderBookResponse(
    val bids: List<OrderBookEntry> = listOf(),
    val asks: List<OrderBookEntry> = listOf()
)