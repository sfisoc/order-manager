package model.responses.dto

data class OrderBookResponse(
    val bids: List<OrderBookEntry> = listOf(),
    val asks: List<OrderBookEntry> = listOf()
)