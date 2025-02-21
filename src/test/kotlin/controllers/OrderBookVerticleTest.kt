package controllers

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.example.constants.Constants.Companion.ACCEPT
import org.example.constants.Constants.Companion.APPLICATION_JSON
import org.example.constants.Constants.Companion.CONTENT_TYPE
import org.example.controllers.OrderBookVerticle
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


private const val ORDER_BOOK_BTCZAR_REQUEST_URL = "/v1/BTCZAR/orderbook"

@ExtendWith(
 VertxExtension::class
)
class OrderBookVerticleTest {

 private lateinit var vertx: Vertx
 private lateinit var webClient: WebClient
 private val port = 8890

 @BeforeEach
 fun setUp(testContext: VertxTestContext) {

  vertx = Vertx.vertx()

  vertx.deployVerticle(OrderBookVerticle(), testContext.succeeding { _ ->
   webClient = WebClient.create(vertx, WebClientOptions().setDefaultPort(port))
   testContext.completeNow()
  })

 }

 @AfterEach
 fun tearDown(testContext: VertxTestContext) {
  vertx.close().onComplete { testContext.completeNow() }
 }

 @Test
 fun testCreateOrder(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  val orderPayload = JsonObject()
   .put("side", "BUY")
   .put("price", "100.50")
   .put("quantity", "2")
   .put("timeInForce", "GTC")

  webClient.post("${ORDER_BOOK_BTCZAR_REQUEST_URL}/limit")
   .putHeader(CONTENT_TYPE, APPLICATION_JSON)
   .sendJsonObject(orderPayload)
   .onSuccess { response ->
    assertEquals(200, response.statusCode())
    assertNotNull(response.bodyAsJsonObject().getString("id"))
    checkpoint.flag()
   }
   .onFailure(testContext::failNow)
 }

 @Test
 fun testCreateOrderBadRequest(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  val orderPayload = JsonObject()
   .put("side", "BUYs")
   .put("price", "100.50")
   .put("quantity", "2")
   .put("timeInForce", "GTC")

  webClient.post("${ORDER_BOOK_BTCZAR_REQUEST_URL}/limit")
   .putHeader(CONTENT_TYPE, APPLICATION_JSON)
   .sendJsonObject(orderPayload)
   .onSuccess { response ->
    assertEquals(400, response.statusCode())
    checkpoint.flag()
   }
   .onFailure(testContext::failNow)
 }


 @Test
 fun testCancelOrder(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  val orderPayload = JsonObject()
   .put("side", "BUY")
   .put("price", "100.50")
   .put("quantity", "2")
   .put("timeInForce", "GTC")

  webClient.post("${ORDER_BOOK_BTCZAR_REQUEST_URL}/limit")
   .putHeader(CONTENT_TYPE, APPLICATION_JSON)
   .sendJsonObject(orderPayload)
   .onSuccess { response ->
    assertEquals(200, response.statusCode())
    val orderId = response.bodyAsJsonObject().getString("id")

    val cancelPayload = JsonObject().put("orderId", orderId)

    webClient.delete("${ORDER_BOOK_BTCZAR_REQUEST_URL}/cancel")
     .putHeader(CONTENT_TYPE, APPLICATION_JSON)
     .sendJsonObject(cancelPayload)
     .onSuccess { response2 ->
      assertEquals(200, response2.statusCode())
      checkpoint.flag()
     }
     .onFailure(testContext::failNow)
   }
   .onFailure(testContext::failNow)
 }

 @Test
 fun testCancelOrderBadRequest(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  val orderPayload = JsonObject()
   .put("side", "BUY")
   .put("price", "100.50")
   .put("quantity", "2")
   .put("timeInForce", "GTC")

  webClient.post("${ORDER_BOOK_BTCZAR_REQUEST_URL}/limit")
   .putHeader(CONTENT_TYPE, APPLICATION_JSON)
   .sendJsonObject(orderPayload)
   .onSuccess { response ->
    assertEquals(200, response.statusCode())
    val orderId = response.bodyAsJsonObject().getString("id")

    val cancelPayload = JsonObject().put("orderIds", orderId)

    webClient.delete("${ORDER_BOOK_BTCZAR_REQUEST_URL}/cancel")
     .putHeader(CONTENT_TYPE, APPLICATION_JSON)
     .sendJsonObject(cancelPayload)
     .onSuccess { response2 ->
      assertEquals(400, response2.statusCode())
      checkpoint.flag()
     }
     .onFailure(testContext::failNow)
   }
   .onFailure(testContext::failNow)
 }


 @Test
 fun testGetOrderBook(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  webClient.get(ORDER_BOOK_BTCZAR_REQUEST_URL)
   .putHeader(ACCEPT, APPLICATION_JSON)
   .send()
   .onSuccess { response ->
    assertEquals(200, response.statusCode())
    assertTrue(response.bodyAsJsonObject().containsKey("bids"))
    assertTrue(response.bodyAsJsonObject().containsKey("asks"))
    checkpoint.flag()
   }
   .onFailure(testContext::failNow)
 }

 @Test
 fun testGetOrderBookBadRequest(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  webClient.get("/v1/BTCZARS/orderbook")
   .putHeader(ACCEPT, APPLICATION_JSON)
   .send()
   .onSuccess { response ->
    assertEquals(400, response.statusCode())
    checkpoint.flag()
   }
   .onFailure(testContext::failNow)
 }

 @Test
 fun testGetTradeHistory(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  webClient.get("/v1/BTCZAR/tradehistory/10")
   .putHeader(ACCEPT, APPLICATION_JSON)
   .send()
   .onSuccess { response ->
    assertEquals(200, response.statusCode())
    assertTrue(response.bodyAsJsonArray().isEmpty)
    checkpoint.flag()
   }
   .onFailure(testContext::failNow)
 }

 @Test
 fun testGetTradeHistoryBadRequest(testContext: VertxTestContext) {
  val checkpoint: Checkpoint = testContext.checkpoint()

  webClient.get("/v1/BTCZARS/tradehistory/10")
   .putHeader(ACCEPT, APPLICATION_JSON)
   .send()
   .onSuccess { response ->
    assertEquals(400, response.statusCode())
    checkpoint.flag()
   }
   .onFailure(testContext::failNow)
 }

}