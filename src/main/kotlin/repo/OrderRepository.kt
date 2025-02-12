package org.example.repo

import model.enums.Side
import org.example.model.entities.Order
import java.math.BigDecimal

interface OrderRepository {
    fun addOrder(order: Order)
    fun removeOrder(orderId: String): Order?
    fun getOrder(orderId: String): Order?
    fun getBestBuy(): Order?
    fun getBestSell(): Order?
    fun getAllBuys(): List<Order>
    fun getAllSells(): List<Order>
    fun updateOrder(order: Order)
    fun getOrdersByPrice(side: Side, price: BigDecimal): List<Order>
}