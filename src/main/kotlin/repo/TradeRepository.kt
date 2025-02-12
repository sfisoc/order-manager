package org.example.repo

import org.example.model.entities.Trade

interface TradeRepository {
    fun addTrade(trade: Trade)
    fun getRecentTrades(limit: Int): List<Trade>
}