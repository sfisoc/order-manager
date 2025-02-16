package model.enums;

enum class OrderSide {
    BUY, SELL
}

enum class OrderStatus {
    ACTIVE, OPEN, FILLED, PARTIALLY_FILLED, CANCELLED, EXPIRED
}

enum class CurrencyPair {
    BTCZAR, ETHZAR, ETHBTC
}

enum class TimeInForce {
    GTC, FOK, IOC
}