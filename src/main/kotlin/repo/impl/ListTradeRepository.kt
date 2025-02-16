package org.example.repo.impl

import org.example.model.entities.Trade
import org.example.repo.TradeRepository

class ListTradeRepository : TradeRepository {

    private val trades = mutableListOf<Trade>()


    override fun addTrade(trade: Trade) {
        trades.add(trade)
    }

    override fun getRecentTrades(limit: Int): List<Trade> {
        return trades.takeLast(limit).reversed()
    }
}