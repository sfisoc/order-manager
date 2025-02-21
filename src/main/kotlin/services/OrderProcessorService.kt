package org.example.services

import model.enums.CurrencyPair
import model.responses.dto.OrderTradesResponse
import org.example.domain.OrderBook
import org.example.model.entities.Order
import model.responses.dto.OrderBookResponse
import org.example.model.responses.internal.OrderResult
import org.example.repo.impl.ListTradeRepository
import org.example.repo.impl.TreeMapOrderRepository

class OrderProcessorService {

    private val orderBookMap = HashMap<CurrencyPair,OrderBook>()

    fun processOrder(currencyPair: CurrencyPair, order: Order): OrderResult? {
        val orderBook = getOrCreateOrderBook(currencyPair)

        return orderBook?.submitOrder(order)
    }

    fun getOrderBook(currencyPair: CurrencyPair): OrderBookResponse {
        val orderBook = getOrCreateOrderBook(currencyPair)

        return orderBook?.getOrderBook() ?:  OrderBookResponse()
    }

    fun getOrderBookTrades(currencyPair: CurrencyPair, limit : Int = 5): List<OrderTradesResponse> {
        val orderBook = getOrCreateOrderBook(currencyPair)

        return orderBook?.getRecentTrades(limit) ?: emptyList()

    }

    fun deleteOrder(currencyPair: CurrencyPair, orderId: String): Boolean {
        val orderBook = getOrCreateOrderBook(currencyPair)

        return orderBook?.cancelOrder(orderId) ?: false
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