package org.example.repo

import model.enums.OrderSide
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
    fun getOrdersByPrice(orderSide: OrderSide, price: BigDecimal): List<Order>
}