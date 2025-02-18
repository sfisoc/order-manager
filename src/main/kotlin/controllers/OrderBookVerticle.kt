package org.example.controllers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.json.get
import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.TimeInForce
import org.example.model.entities.Order
import org.example.model.responses.dto.ErrorResponse
import org.example.services.OrderProcessorService

private const val CONTENT_TYPE = "content-type"

private const val APPLICATION_JSON = "application/json"

class OrderBookVerticle : AbstractVerticle() {

    private val orderService = OrderProcessorService()
    private val router = Router.router(Vertx.vertx())

    init {

        router.route().handler(BodyHandler.create());

        val objectMapper = DatabindCodec.mapper()

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)

        val module = JavaTimeModule()
        objectMapper.registerModule(module)
    }

    override fun start() {

        setupRoutes()

        vertx.createHttpServer(
            HttpServerOptions().
            setPort(8890).
            setHost("localhost")
        ).requestHandler(router)
        .listen()
        print("Server started on 8890")
    }

    private fun setupRoutes() {

        router.post("/v1/:currencyPair/orderbook/limit")
            .consumes(APPLICATION_JSON)
            .handler(this::createOrder);

        router.post("/v1/:currencyPair/orderbook/cancel")
            .handler(this::cancelOrder);

        router.get("/v1/:currencyPair/orderbook")
            .handler(this::getOrderBook);

        router.get("/v1/:currencyPair/tradehistory/:limit")
            .handler(this::getOrderBookTradeHistory);
    }

    private fun createOrder(routingContext: RoutingContext) {

        val currencyPair: String = routingContext.request()
            .getParam("currencyPair")

        val pair = CurrencyPair.valueOf(currencyPair)

        val jsonObject = routingContext.body().asJsonObject()

        val getSide = jsonObject.get<String>("side")
        val getPrice = jsonObject.get<String>("price")
        val getQuantity = jsonObject.get<String>("quantity")

        val getTimeInForce = jsonObject.get<String>("timeInForce")

        val order = Order(
            side = OrderSide.valueOf(getSide),
            price = getPrice.toBigDecimal(),
            quantity = getQuantity.toBigDecimal(),
            currencyPair = pair,
            timeInForce = TimeInForce.valueOf(getTimeInForce)
        )


        val orderResult = orderService.processOrder(pair, order)

        if (orderResult?.order != null) {

                routingContext.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .setStatusCode(200)
                    .end(Json.encodePrettily(orderResult.order))

        }
        else
        {
            routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .setStatusCode(400)
                .end(Json.encodePrettily(ErrorResponse(order.id," Failed to process Order")))

        }

    }

    private fun cancelOrder(routingContext: RoutingContext) {

        val currencyPair: String = routingContext.request()
            .getParam("currencyPair")

        val pair = CurrencyPair.valueOf(currencyPair)

        val jsonObject = routingContext.body().asJsonObject()


        val getOrderId = jsonObject.get<String>("orderId")

        if(getOrderId.isNotEmpty())
        {
            val deleteOrder = orderService.deleteOrder(pair, getOrderId)

            if(deleteOrder)
            {
                routingContext.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .setStatusCode(200)
                    .end()
            }
            else
            {
                routingContext.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .setStatusCode(400)
                    .end()
            }
        }
        else
        {
            routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .setStatusCode(400)
                .end()
        }
    }

    private fun getOrderBook(routingContext: RoutingContext) {

        val currencyPair: String = routingContext.request()
            .getParam("currencyPair")

        val pair = CurrencyPair.valueOf(currencyPair)

        val orderBookResponse = orderService.getOrderBook(pair)

        routingContext.response()
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .setStatusCode(200)
            .end(Json.encodePrettily(orderBookResponse))
    }

    private fun getOrderBookTradeHistory(routingContext: RoutingContext) {

        val currencyPair: String = routingContext.request()
            .getParam("currencyPair")

        val pair = CurrencyPair.valueOf(currencyPair)

        val limit: String = routingContext.request()
            .getParam("limit")

        var limitNum: Int  = 5

        if(limit.isNotEmpty())
        {
            limitNum = limit.toInt()
        }

        val tradesResponse = orderService.getOrderBookTrades(pair, limitNum)

        routingContext.response()
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .setStatusCode(200)
            .end(Json.encodePrettily(tradesResponse))
    }



}