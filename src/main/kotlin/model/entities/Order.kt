package org.example.model.entities

import model.enums.Side

data class Order (val id: String,
                 val side: Side,
                 val quantity: Double,
                 val price: String,
                 val currencyPair: String,
                 val orderCount: Int
)


//     "side": "sell",
//            "quantity": "0.00006",
//            "price": "1852331",
//            "currencyPair": "BTCZAR",
//            "orderCount": 1
