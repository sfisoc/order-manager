package org.example.services

import model.enums.CurrencyPair
import org.example.domain.OrderBook
import org.example.model.entities.Order
import org.example.repo.impl.ListTradeRepository
import org.example.repo.impl.TreeMapOrderRepository

class OrderProcessor {

    private val orderBookMap = HashMap<CurrencyPair,OrderBook>()

    fun processOrder(currencyPair: CurrencyPair, order: Order)
    {
        val orderBook = getOrCreateOrderBook(currencyPair)

        orderBook?.submitOrder(order)
    }

    private fun getOrCreateOrderBook(currencyPair: CurrencyPair): OrderBook? {
        if(orderBookMap.containsKey(currencyPair))
        {
            return orderBookMap[currencyPair]
        }
        else
        {
            //todo dynamically create to when other repo implementation exist
            val orderBook = OrderBook(currencyPair, TreeMapOrderRepository(),ListTradeRepository())

            orderBookMap[currencyPair] = orderBook

            return orderBookMap[currencyPair]
        }
    }

}