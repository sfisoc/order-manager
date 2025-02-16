package org.example.repo.impl

import model.enums.OrderSide
import org.example.model.entities.Order
import org.example.repo.OrderRepository
import java.math.BigDecimal
import java.util.*

class TreeMapOrderRepository : OrderRepository {
    private val buyOrders = TreeMap<BigDecimal, MutableList<Order>>(reverseOrder())
    private val sellOrders = TreeMap<BigDecimal, MutableList<Order>>()
    private val orderMap = mutableMapOf<String, Order>()

    override fun addOrder(order: Order) {
        val orders = getOrderListBySide(order.side)

        orders.getOrPut(order.price) { mutableListOf() }
            .add(order)

        orderMap[order.id] = order
    }

    override fun removeOrder(orderId: String): Order? {
        val order = orderMap[orderId] ?: return null

        val orders = getOrderListBySide(order.side)

        orders[order.price]?.remove(order)

        return orderMap.remove(orderId)
    }

    override fun getOrder(orderId: String): Order? {
        return orderMap[orderId]
    }

    override fun getBestBuy(): Order? {
        return buyOrders.firstNotNullOfOrNull { it.value.minByOrNull { order -> order.timestamp }
        }
    }

    override fun getBestSell(): Order? {
       return sellOrders.firstNotNullOfOrNull { it.value.minByOrNull { order -> order.timestamp } }
    }

    override fun getAllBuys(): List<Order> {
        return buyOrders.values.flatten().sortedWith(compareByDescending<Order> { it.price }
            .thenBy { it.timestamp } )
    }

    override fun getAllSells(): List<Order> {
        return sellOrders.values.flatten().sortedWith(compareBy<Order> { it.price }
            .thenBy { it.timestamp })
    }

    override fun updateOrder(order: Order) {
        removeOrder(order.id)
        addOrder(order)
    }

    override fun getOrdersByPrice(orderSide: OrderSide, price: BigDecimal): List<Order> {
        return when (orderSide) {
            OrderSide.BUY -> buyOrders[price] ?: emptyList()
            OrderSide.SELL -> sellOrders[price] ?: emptyList()
        }
    }

    private fun getOrderListBySide(orderSide : OrderSide): TreeMap<BigDecimal, MutableList<Order>>
    {
       return  when (orderSide) {
            OrderSide.BUY -> buyOrders
            OrderSide.SELL -> sellOrders
        }
    }
}