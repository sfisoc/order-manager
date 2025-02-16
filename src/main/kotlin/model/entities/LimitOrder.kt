package org.example.model.entities

import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.TimeInForce

data class LimitOrder (val id: String,
                       val orderSide: OrderSide,
                       val quantity: Double,
                       val price: String,
                       val pair: CurrencyPair,
                       val postOnly: Boolean,
                       val customerOrderId: Int,
                       val timeInForce: TimeInForce
)


//{
//    "side": "SELL",
//    "quantity": "0.100000",
//    "price": "10000",
//    "pair": "BTCZAR",
//    "postOnly": true,
//    "customerOrderId": "1234"
//    "timeInForce": "GTC"
//}