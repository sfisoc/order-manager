package org.example.model.entities

import model.enums.Side
import model.enums.timeInForce

data class LimitOrder (val id: String,
                       val side: Side,
                       val quantity: Double,
                       val price: String,
                       val pair: String,
                       val postOnly: Boolean,
                       val customerOrderId: Int,
                       val timeInForce: timeInForce
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