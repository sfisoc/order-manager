package org.example

import io.vertx.core.Vertx
import org.example.controllers.OrderBookVerticle

fun main() {
    val name = "Order Manager - Order Book Engine"

    println("Welcome to the  $name !!!!!!!!!!!!!!!!!")

    val vertx = Vertx.vertx()
    vertx.deployVerticle(OrderBookVerticle())

}