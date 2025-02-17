package domain

import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.OrderStatus
import model.enums.TimeInForce
import org.example.domain.OrderBook
import org.example.model.entities.Order
import org.example.repo.impl.ListTradeRepository
import org.example.repo.impl.TreeMapOrderRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class OrderBookTest {

  private lateinit var orderBook: OrderBook

  @BeforeEach
  fun setup() {

   orderBook = OrderBook(
       orderBookCurrencyPair = CurrencyPair.BTCZAR,
       orderRepository = TreeMapOrderRepository(),
       tradeRepository = ListTradeRepository()
   )

  }
@Test
 fun `submitOrder no matching`() {

 val order = Order(
     side = OrderSide.BUY,
     price = BigDecimal("50000"),
     quantity = BigDecimal("1"),
     currencyPair = CurrencyPair.BTCZAR,
 )

 val (resultOrder, trades) = orderBook.submitOrder(order)

 assertEquals(OrderStatus.ACTIVE, resultOrder!!.status)
 assertTrue(trades!!.isEmpty())

 }

@Test
 fun `submit sell order - matches existing buy`() {

 val buyOrder = Order(
  side = OrderSide.BUY,
  price = BigDecimal("50000"),
  quantity = BigDecimal("1"),
  currencyPair = CurrencyPair.BTCZAR,
  )
 orderBook.submitOrder(buyOrder)

 val sellOrder = Order(
  side = OrderSide.SELL,
  price = BigDecimal("50000"),
  quantity = BigDecimal("1"),
  currencyPair = CurrencyPair.BTCZAR,
  )

 val (resultOrder, trades) = orderBook.submitOrder(sellOrder)

 assertEquals(OrderStatus.FILLED, resultOrder!!.status)
 assertEquals(1, trades!!.size)
 assertEquals(BigDecimal("1"), trades[0].quantity)

 }

 @Test
 fun `submit sell order - matches existing buy partially`() {

  val buyOrder = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1"),
   currencyPair = CurrencyPair.BTCZAR,
  )
  orderBook.submitOrder(buyOrder)

  val sellOrder = Order(
   side = OrderSide.SELL,
   price = BigDecimal("40000"),
   quantity = BigDecimal("1.25"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  val (resultOrder, trades) = orderBook.submitOrder(sellOrder)

  assertEquals(OrderStatus.ACTIVE, resultOrder!!.status)
  assertEquals(1, trades!!.size)
  assertEquals(BigDecimal("1"), trades[0].quantity)

 }

 @Test
 fun `submit sell order - matches existing orders`() {

  val sellOrder = Order(
   side = OrderSide.SELL,
   price = BigDecimal("40000"),
   quantity = BigDecimal("1"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(sellOrder)

  val sellOrder2 = Order(
   side = OrderSide.SELL,
   price = BigDecimal("40000"),
   quantity = BigDecimal("0.25"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(sellOrder2)

  val buyOrder = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1.25"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  val (resultOrder, trades) = orderBook.submitOrder(buyOrder)

  assertEquals(OrderStatus.FILLED, resultOrder!!.status)
  assertEquals(2, trades!!.size)
  assertEquals(BigDecimal("0.25"), trades[1].quantity)

 }

 @Test
 fun `submitOrder no matching IOC`() {

  val order = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1"),
   timeInForce = TimeInForce.IOC,
   currencyPair = CurrencyPair.BTCZAR,
  )

  val (resultOrder, trades) = orderBook.submitOrder(order)

  assertEquals(OrderStatus.CANCELLED, resultOrder!!.status)
  assertTrue(trades!!.isEmpty())

 }

 @Test
 fun `submitOrder IOC  filled`() {

  val sellOrder = Order(
   side = OrderSide.SELL,
   price = BigDecimal("40000"),
   quantity = BigDecimal("1"),
   timeInForce = TimeInForce.GTC,
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(sellOrder)

  val order = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1"),
   timeInForce = TimeInForce.IOC,
   currencyPair = CurrencyPair.BTCZAR,
  )

  val (resultOrder, trades) = orderBook.submitOrder(order)

  assertEquals(OrderStatus.FILLED, resultOrder!!.status)
  assertTrue(trades!!.isNotEmpty())
 }

 @Test
 fun `submitOrder no matching FOK`() {

  val order = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1"),
   timeInForce = TimeInForce.FOK,
   currencyPair = CurrencyPair.BTCZAR,
  )

  val (resultOrder, trades) = orderBook.submitOrder(order)

  assertEquals(OrderStatus.CANCELLED, resultOrder!!.status)
  assertTrue(trades!!.isEmpty())

 }

 @Test
 fun `submitOrder FOK  filled`() {

  val sellOrder = Order(
   side = OrderSide.SELL,
   price = BigDecimal("49999"),
   quantity = BigDecimal("1"),
   timeInForce = TimeInForce.GTC,
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(sellOrder)

  val order = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1"),
   timeInForce = TimeInForce.FOK,
   currencyPair = CurrencyPair.BTCZAR,
  )

  val (resultOrder, trades) = orderBook.submitOrder(order)

  assertEquals(OrderStatus.FILLED, resultOrder!!.status)
  assertTrue(trades!!.isNotEmpty())
 }

 @Test
 fun `cancel order`() {

  val order = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(order)

  val isCanceled= orderBook.cancelOrder(order.id)

  assertTrue(isCanceled)

 }

@Test
 fun getRecentTrades() {

 val sellOrder = Order(
  side = OrderSide.SELL,
  price = BigDecimal("40000"),
  quantity = BigDecimal("1"),
  currencyPair = CurrencyPair.BTCZAR,
 )

 orderBook.submitOrder(sellOrder)

 val sellOrder2 = Order(
  side = OrderSide.SELL,
  price = BigDecimal("40000"),
  quantity = BigDecimal("0.25"),
  currencyPair = CurrencyPair.BTCZAR,
 )

 orderBook.submitOrder(sellOrder2)

 val buyOrder = Order(
  side = OrderSide.BUY,
  price = BigDecimal("50000"),
  quantity = BigDecimal("1.25"),
  currencyPair = CurrencyPair.BTCZAR,
 )

 orderBook.submitOrder(buyOrder)

 val buyOrder2 = Order(
  side = OrderSide.BUY,
  price = BigDecimal("50000"),
  quantity = BigDecimal("0.00585"),
  currencyPair = CurrencyPair.BTCZAR,
 )

 orderBook.submitOrder(buyOrder2)

 assertEquals(2, orderBook.getRecentTrades().size)
 }


 @Test
 fun getOrderBook() {

  val sellOrder = Order(
   side = OrderSide.SELL,
   price = BigDecimal("60000"),
   quantity = BigDecimal("1"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(sellOrder)

  val sellOrder2 = Order(
   side = OrderSide.SELL,
   price = BigDecimal("60000"),
   quantity = BigDecimal("0.25"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(sellOrder2)

  val buyOrder = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("1.25"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(buyOrder)

  val buyOrder2 = Order(
   side = OrderSide.BUY,
   price = BigDecimal("50000"),
   quantity = BigDecimal("0.00585"),
   currencyPair = CurrencyPair.BTCZAR,
  )

  orderBook.submitOrder(buyOrder2)

  assertEquals(2, orderBook.getOrderBook().asks.size)
  assertEquals(2, orderBook.getOrderBook().bids.size)


 }


}