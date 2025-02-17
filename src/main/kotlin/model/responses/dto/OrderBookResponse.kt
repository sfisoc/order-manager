package model.responses.dto

import org.example.model.responses.api.OrderBookEntry

data class OrderBookResponse(
    val bids: List<OrderBookEntry> = listOf(),
    val asks: List<OrderBookEntry> = listOf()
)