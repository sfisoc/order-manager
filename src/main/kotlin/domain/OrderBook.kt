package org.example.domain

import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.OrderStatus
import model.enums.TimeInForce
import model.responses.dto.OrderTradesResponse
import org.example.model.entities.Order
import org.example.model.entities.Trade
import org.example.model.responses.api.OrderBookEntry
import org.example.model.responses.api.OrderBookResponse
import org.example.model.responses.internal.OrderMatchResult
import org.example.model.responses.internal.OrderResult
import org.example.repo.OrderRepository
import org.example.repo.TradeRepository
import java.math.BigDecimal

class OrderBook(private val orderBookCurrencyPair: CurrencyPair,
                private val orderRepository: OrderRepository,
                private val tradeRepository: TradeRepository
) {

    fun submitOrder(order : Order): OrderResult {
        return when (order.timeInForce) {
            TimeInForce.GTC -> processGTCOrder(order)
            TimeInForce.IOC -> processIOCOrder(order)
            TimeInForce.FOK -> processFOKOrder(order)
            else -> processGTCOrder(order) //Default
        }
    }

    fun cancelOrder(order: Order): Boolean {
        val removeOrder = orderRepository.removeOrder(order.id)

        return null != removeOrder
    }

    fun getOrderBook(): OrderBookResponse {
        return OrderBookResponse(
            bids = orderRepository.getAllBuys()
                .sortedBy { it.price }
                .map {
                    OrderBookEntry(it.side, it.quantity ,it.price, orderBookCurrencyPair)
                },
            asks = orderRepository.getAllSells()
                .sortedByDescending { it.price }
                .map {
                    OrderBookEntry(it.side, it.quantity ,it.price, orderBookCurrencyPair)
                },
        )
    }

    fun getRecentTrades(limit: Int = 5): List<OrderTradesResponse> {
        return tradeRepository.getRecentTrades(limit).reversed().map { trade: Trade ->
            OrderTradesResponse(trade.id,trade.price,trade.quantity,trade.currencyPair,trade.timestamp)
        }
    }

    private fun processOrderMatch(order: Order): OrderMatchResult {

        var remainingQuantity = order.quantity
        val trades = mutableListOf<Trade>()

        while (remainingQuantity > BigDecimal.ZERO) {
            val matchingOrder = when (order.side) {
                OrderSide.BUY -> orderRepository.getBestSell()
                OrderSide.SELL -> orderRepository.getBestBuy()
            } ?: break

            if ((order.side == OrderSide.BUY && matchingOrder.price > order.price) ||
                (order.side == OrderSide.SELL && matchingOrder.price < order.price)) {
                break
            }

            val tradeQuantity = minOf(remainingQuantity, matchingOrder.quantity)
            val trade = Trade(
                price = matchingOrder.price,
                quantity = tradeQuantity,
                takerOrderId = order.id,
                makerOrderId = matchingOrder.id,
                currencyPair = orderBookCurrencyPair,
            )

            trades.add(trade)
            tradeRepository.addTrade(trade)
            remainingQuantity -= tradeQuantity

            if (matchingOrder.quantity == tradeQuantity) {
                orderRepository.removeOrder(matchingOrder.id)
            } else {
                orderRepository.updateOrder(
                    matchingOrder.copy(
                        quantity = matchingOrder.quantity - tradeQuantity,
                        status = OrderStatus.PARTIALLY_FILLED
                    )
                )
            }
        }

        return OrderMatchResult(remainingQuantity, trades)
    }

    private fun processGTCOrder(order: Order): OrderResult {

        val matchResult = processOrderMatch(order)
        if (matchResult.remainingQuantity > BigDecimal.ZERO) {
            val remainingOrder = order.copy(
                quantity = matchResult.remainingQuantity,
                status = if (matchResult.trades.isEmpty()) OrderStatus.OPEN else OrderStatus.PARTIALLY_FILLED
            )
            orderRepository.addOrder(remainingOrder)
        }
        return OrderResult(
            order = if (matchResult.remainingQuantity > BigDecimal.ZERO) {
                order.copy(quantity = matchResult.remainingQuantity)
            } else {
                order.copy(status = OrderStatus.FILLED)
            },
            trades = matchResult.trades
        )
    }

    private fun processIOCOrder(order: Order): OrderResult {

        val matchResult = processOrderMatch(order)

       val finalOrder = if (matchResult.remainingQuantity > BigDecimal.ZERO) {
            order.copy(status = OrderStatus.CANCELLED)
        }
        else
        {
            order.copy(status = OrderStatus.FILLED)
        }

        return OrderResult(finalOrder, matchResult.trades)
    }

    private fun processFOKOrder(order: Order): OrderResult {

        val simulation = simulateOrderMatch(order)

        if (simulation.remainingQuantity == BigDecimal.ZERO) {

            val matchOrder = processOrderMatch(order)

            return OrderResult(order.copy(status = OrderStatus.FILLED),
                matchOrder.trades)

        } else {

            return OrderResult(
                order.copy(status = OrderStatus.CANCELLED),
                emptyList()
            )
        }
    }

    private fun simulateOrderMatch(order: Order): OrderMatchResult {
        var remainingQuantity = order.quantity
        val possibleTrades = mutableListOf<Trade>()

        val relevantSideOrders = when (order.side) {
            OrderSide.BUY -> orderRepository.getAllSells()
            OrderSide.SELL -> orderRepository.getAllBuys()
        }

        for (possibleOrder in relevantSideOrders) {

            if ((order.side == OrderSide.BUY && possibleOrder.price > order.price) ||
                (order.side == OrderSide.SELL && possibleOrder.price < order.price)) {
                break
            }

            val tradeQuantity = minOf(remainingQuantity, possibleOrder.quantity)

            possibleTrades.add(
                Trade(
                    price = possibleOrder.price,
                    quantity = tradeQuantity,
                    takerOrderId = order.id,
                    makerOrderId = possibleOrder.id,
                    currencyPair = orderBookCurrencyPair
                )
            )

            remainingQuantity -= tradeQuantity

            if (remainingQuantity == BigDecimal.ZERO) break
        }

        return OrderMatchResult(remainingQuantity, possibleTrades)
    }

}