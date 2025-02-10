package org.example.model.entities

import model.enums.Side

data class Trade (val id: String,
                  val price: String,
                  val quantity: Double,
                  val currencyPair: String,
                  val tradeAt: String,
                  val takerSide: Side,
                  val sequenceId: String,
                  val quoteVolume: String
)

// "price": "43064",
//    "quantity": "0.00079928",
//    "currencyPair": "BTCUSDC",
//    "tradedAt": "2024-02-05T07:47:04.625Z",
//    "takerSide": "sell",
//    "sequenceId": 1203970033324130300,
//    "id": "c13c5166-c3fa-11ee-b1a8-c700095e5df0",
//    "quoteVolume": "34.42019392"