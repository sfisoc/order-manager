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
import io.vertx.ext.web.validation.BadRequestException
import io.vertx.ext.web.validation.BodyProcessorException
import io.vertx.ext.web.validation.ParameterProcessorException
import io.vertx.ext.web.validation.RequestPredicateException
import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.TimeInForce
import org.example.model.entities.Order
import org.example.model.responses.dto.ErrorResponse
import org.example.services.OrderProcessorService
import org.example.utils.Validator


private const val CONTENT_TYPE = "content-type"

private const val APPLICATION_JSON = "application/json"

class OrderBookVerticle : AbstractVerticle() {

    private val orderService = OrderProcessorService()
    private val router = Router.router(Vertx.vertx())

    init {

        router.route().handler(BodyHandler.create().setBodyLimit(100));

        manageFailure(router)

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
            .handler(Validator.currencyPairValidator(vertx).build() )
            .handler(Validator.orderPayloadValidator(vertx).build() )
            .handler(this::createOrder);

        router.post("/v1/:currencyPair/orderbook/cancel")
            .consumes(APPLICATION_JSON)
            .handler(Validator.currencyPairValidator(vertx).build() )
            .handler(Validator.orderCancelPayloadValidator(vertx).build() )
            .handler(this::cancelOrder);

        router.get("/v1/:currencyPair/orderbook")
            .produces(APPLICATION_JSON)
            .handler(Validator.currencyPairValidator(vertx).build() )
            .handler(this::getOrderBook);

        router.get("/v1/:currencyPair/tradehistory/:limit")
            .produces(APPLICATION_JSON)
            .handler(Validator.currencyPairValidator(vertx).build() )
            .handler(this::getOrderBookTradeHistory);
    }

    private fun manageFailure(router: Router) {
        router.errorHandler(
            400
        ) { routingContext: RoutingContext ->
            if (routingContext.failure() is BadRequestException) {
                if (routingContext.failure() is ParameterProcessorException) {
                    // Something went wrong while parsing/validating a
                    routingContext.response()
                        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .setStatusCode(400)
                        .end(Json.encodePrettily(ErrorResponse(message = "Malformed paramter")))

                } else if (routingContext.failure() is BodyProcessorException) {
                    // Something went wrong while parsing/validating the body

                    routingContext.response()
                        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .setStatusCode(400)
                        .end(Json.encodePrettily(ErrorResponse(message = "Invalid Body")))

                } else if (routingContext.failure() is RequestPredicateException) {
                    // A request predicate is unsatisfied

                    routingContext.response()
                        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .setStatusCode(400)
                        .end(Json.encodePrettily(ErrorResponse(message = "Body required")))
                }
            }
        }
    }


        private fun createOrder(routingContext: RoutingContext) {

        val isHeaderValid = Validator.isValidateHeader(APPLICATION_JSON,routingContext.request().getHeader(CONTENT_TYPE))


        val currencyPair: String = routingContext.request()
            .getParam("currencyPair")

        val pair = CurrencyPair.valueOf(currencyPair)

        val jsonObject = routingContext.body().asJsonObject()

        val getSide = jsonObject.getString("side")
        val getPrice = jsonObject.getString("price")
        val getQuantity = jsonObject.getString("quantity")

        val getTimeInForce = jsonObject.getString("timeInForce")

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

        val getOrderId = jsonObject.getString("orderId")

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