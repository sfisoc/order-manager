package org.example.model.responses

import org.example.model.entities.Order
import org.example.model.entities.Trade

data class OrderResult(
    val order: Order?,
    val trades: List<Trade>?
)