package org.example.utils


import io.vertx.core.Vertx
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.TimeInForce
import io.vertx.json.schema.common.dsl.Schemas.*


class  Validator {

    companion object Factory {

         fun isValidateHeader(validHeader: String, header: String): Boolean {

            return header.isNotEmpty() && header.equals(validHeader, ignoreCase = true)
        }

         fun isValidatePositiveStringNumber(input: String): Boolean {

            return input.isNotEmpty() && input.matches("/^\\d+(\\.\\d+)?\$/gm".toRegex())
        }

         fun isValidateStringSide(input: String): Boolean {

            return input.isNotEmpty() && (input == OrderSide.SELL.toString() || input == OrderSide.SELL.toString())
        }

         fun isValidateStringTimeOfForce(input: String): Boolean {

            return input.isNotEmpty() && (input == TimeInForce.GTC.toString() || input == TimeInForce.FOK.toString() || input == TimeInForce.IOC.toString())
        }

        fun isValidateStringCurrencyPair(input: String): Boolean {

            return input.isNotEmpty() && (input == CurrencyPair.BTCZAR.toString() || input == CurrencyPair.ETHZAR.toString() || input == CurrencyPair.ETHBTC.toString() )
        }

        private fun initSchemaParser(vertx : Vertx): SchemaParser? {

            //Using dprecated methods beacuse of a git issue https://github.com/vert-x3/vertx-web/issues/2437
            val schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
            val schemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter)

            return schemaParser
        }

        fun currencyPairValidator(vertx : Vertx): ValidationHandlerBuilder {
            return ValidationHandlerBuilder
                .create(initSchemaParser(vertx))
                .pathParameter(Parameters.param("currencyPair", intSchema()))
        }

        fun orderPayloadValidator(vertx : Vertx): ValidationHandlerBuilder {

            val bodySchemaBuilder = objectSchema()
                .requiredProperty("side", stringSchema()) //todo add valid enums to props
                .requiredProperty("price", stringSchema())
                .requiredProperty("quantity", stringSchema())
                .optionalProperty("timeInForce", stringSchema())

            return ValidationHandlerBuilder
                .create(initSchemaParser(vertx))
                .body(Bodies.json(bodySchemaBuilder))
                .predicate(RequestPredicate.BODY_REQUIRED);

        }

        fun orderCancelPayloadValidator(vertx : Vertx): ValidationHandlerBuilder {

            val bodySchemaBuilder = objectSchema()
                .requiredProperty("orderId", stringSchema())

            return ValidationHandlerBuilder
                .create(initSchemaParser(vertx))
                .body(Bodies.json(bodySchemaBuilder))
                .predicate(RequestPredicate.BODY_REQUIRED);

        }


    }


}