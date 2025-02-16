package repo.impl

import model.enums.CurrencyPair
import model.enums.OrderSide
import org.example.model.entities.Order
import org.example.repo.impl.TreeMapOrderRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class TreeMapOrderRepositoryTest {

 private lateinit var inMemoryDb : TreeMapOrderRepository
 private lateinit var buyOrderList: List<Order>
 private lateinit var sellOrderList: List<Order>

 @BeforeEach
 fun setUp() {

  inMemoryDb = TreeMapOrderRepository()

  val order1 = Order(
   UUID.randomUUID().toString(),
   OrderSide.BUY,
   BigDecimal(897845.00),
   BigDecimal(895586.00),
   CurrencyPair.BTCZAR,
   LocalDateTime.now()
  )

  val order2 = Order(
   UUID.randomUUID().toString(),
   OrderSide.BUY,
   BigDecimal(99785.00),
   BigDecimal(69576.00),
   CurrencyPair.BTCZAR,
   LocalDateTime.now()
  )

  val order3 = Order(
   UUID.randomUUID().toString(),
   OrderSide.BUY,
   BigDecimal(89785.00),
   BigDecimal(1000.00),
   CurrencyPair.BTCZAR,

   LocalDateTime.now()
  )

  buyOrderList = listOf(order1,order2,order3)

  val order4 = Order(
   UUID.randomUUID().toString(),
   OrderSide.SELL,
   BigDecimal(897845.00),
   BigDecimal(895586.00),
   CurrencyPair.BTCZAR,
   LocalDateTime.now()
  )

  val order5 = Order(
   UUID.randomUUID().toString(),
   OrderSide.SELL,
   BigDecimal(99785.00),
   BigDecimal(69576.00),
   CurrencyPair.BTCZAR,
   LocalDateTime.now()
  )

  val order6 = Order(
   UUID.randomUUID().toString(),
   OrderSide.SELL,
   BigDecimal(89785.00),
   BigDecimal(1000.00),
   CurrencyPair.BTCZAR,
   LocalDateTime.now()
  )

  sellOrderList = listOf(order4,order5,order6)

 }

 private fun  populateDB()
 {
  inMemoryDb.addOrder(buyOrderList[0])
  inMemoryDb.addOrder(buyOrderList[1])
  inMemoryDb.addOrder(buyOrderList[2])

  inMemoryDb.addOrder(sellOrderList[0])
  inMemoryDb.addOrder(sellOrderList[1])
  inMemoryDb.addOrder(sellOrderList[2])

 }

@Test
 fun addOrder() {

 val order = Order(
  UUID.randomUUID().toString(),
  OrderSide.BUY,
  BigDecimal(89785.00),
  BigDecimal(89576.00),
  CurrencyPair.BTCZAR,
  LocalDateTime.now()
 )

 inMemoryDb.addOrder(order);

 assertEquals(order.id, inMemoryDb.getOrder(order.id)?.id ?: "none")
}

@Test
 fun removeOrder() {

 val order = Order(
  UUID.randomUUID().toString(),
  OrderSide.BUY,
  BigDecimal(89785.00),
  BigDecimal(89576.00),
  CurrencyPair.BTCZAR,
  LocalDateTime.now()
 )

 inMemoryDb.addOrder(order);

 val removeOrder = inMemoryDb.removeOrder(order.id)

 assertEquals(order.id,removeOrder?.id)
}

@Test
 fun getBestBuy() {

 inMemoryDb.addOrder(buyOrderList[0])
 inMemoryDb.addOrder(buyOrderList[1])
 inMemoryDb.addOrder(buyOrderList[2])

 val bestBuy = inMemoryDb.getBestBuy()

 assertEquals(buyOrderList[0].id,bestBuy?.id)

}

@Test
 fun getBestSell() {

 inMemoryDb.addOrder(sellOrderList[0])
 inMemoryDb.addOrder(sellOrderList[1])
 inMemoryDb.addOrder(sellOrderList[2])

 val bestSell = inMemoryDb.getBestSell()

 assertEquals(sellOrderList[2].id,bestSell?.id)

 }

@Test
 fun getAllBuys() {

 populateDB()

 val allBuys = inMemoryDb.getAllBuys()

 assertEquals(buyOrderList.size,allBuys.size)

 }

@Test
 fun getAllSells() {

 populateDB()

 val allSells = inMemoryDb.getAllSells()

 assertEquals(buyOrderList.size,allSells.size)

 }

@Test
 fun updateOrder() {

  val order = inMemoryDb.updateOrder(sellOrderList[0])

 assertNotNull(order)

 }

@Test
 fun getOrdersByPrice() {

  populateDB()

 val ordersByPrice = inMemoryDb.getOrdersByPrice(OrderSide.BUY, buyOrderList[1].price)

 assertTrue(ordersByPrice.isNotEmpty())

}

}