package org.example

import io.vertx.core.Vertx
import org.example.controllers.OrderBookVerticle

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val name = "Order Manager - Order Book Engine"
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    println("Welcome to the  $name !!!!!!!!!!!!!!!!!")

    val vertx = Vertx.vertx()
    vertx.deployVerticle(OrderBookVerticle())

}