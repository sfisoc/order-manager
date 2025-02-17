package org.example.model.responses.api

data class OrderBookResponse(
    val bids: List<OrderBookEntry>,
    val asks: List<OrderBookEntry>
)