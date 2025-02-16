package org.example.domain

import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.OrderStatus
import model.enums.TimeInForce
import org.example.model.entities.Order
import org.example.model.entities.Trade
import org.example.model.responses.OrderMatchResult
import org.example.model.responses.OrderResult
import org.example.repo.OrderRepository
import org.example.repo.TradeRepository
import java.math.BigDecimal

class OrderBook(private val currencyPair: CurrencyPair,
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

    private fun matchOrder(order: Order): OrderMatchResult {

        var remainingQuantity = order.quantity
        val trades = mutableListOf<Trade>()

        while (remainingQuantity > BigDecimal.ZERO) {
            val matchingOrder = when (order.side) {
                OrderSide.BUY -> orderRepository.getBestBuy()
                OrderSide.SELL -> orderRepository.getBestSell()
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
                currencyPair = order.currencyPair,
                takerOrderSide = order.side,
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

    fun removeOrder()
    {

    }

    fun getOrderBook()
    {

    }

    fun getOrderTrades()
    {

    }

    private fun matchOrders()
    {

    }

    private fun processGTCOrder(order: Order): OrderResult {

        return OrderResult(null,null)

    }

    private fun processIOCOrder(order: Order): OrderResult {

        return OrderResult(null,null)

    }

    private fun processFOKOrder(order: Order): OrderResult {

        return OrderResult(null,null)

    }
}