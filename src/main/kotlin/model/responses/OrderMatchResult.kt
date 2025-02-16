package org.example.model.responses

import org.example.model.entities.Trade
import java.math.BigDecimal

data class OrderMatchResult (
    val remainingQuantity: BigDecimal,
    val trades: List<Trade>
)