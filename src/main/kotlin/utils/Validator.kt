package org.example.utils

import io.vertx.core.Vertx
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.Parameters
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaParser
import io.vertx.json.schema.SchemaRouter
import io.vertx.json.schema.SchemaRouterOptions
import io.vertx.json.schema.common.dsl.Keywords.*
import model.enums.CurrencyPair
import model.enums.OrderSide
import model.enums.TimeInForce
import io.vertx.json.schema.common.dsl.Schemas.*
import java.util.regex.Pattern

class  Validator {

    companion object Factory {

        private fun initSchemaParser(vertx : Vertx): SchemaParser? {

            //Using deprecated methods because of a git issue https://github.com/vert-x3/vertx-web/issues/2437
            val schemaRouter = SchemaRouter.create(vertx, SchemaRouterOptions())
            val schemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter)

            return schemaParser
        }

        fun currencyPairValidator(vertx : Vertx): ValidationHandlerBuilder {

            val currencyPairSchema = arraySchema()
                .items(enumSchema(*CurrencyPair.values().map { it.name }.toTypedArray()))

            return ValidationHandlerBuilder
                .create(initSchemaParser(vertx))
                .pathParameter(Parameters.param("currencyPair",currencyPairSchema))
        }

        fun orderPayloadValidator(vertx : Vertx): ValidationHandlerBuilder {

            val bodySchemaBuilder = objectSchema()
                .requiredProperty("side", enumSchema(OrderSide.SELL.name,OrderSide.BUY.name))
                .requiredProperty("price", stringSchema().with(pattern(Pattern.compile("^(0|[1-9]\\d*)(\\.\\d+)?$"))))
                .requiredProperty("quantity", stringSchema().with(pattern(Pattern.compile("^(0|[1-9]\\d*)(\\.\\d+)?$"))))
                .optionalProperty("timeInForce", enumSchema(TimeInForce.IOC.name,TimeInForce.GTC.name,TimeInForce.FOK.name))

            return ValidationHandlerBuilder
                .create(initSchemaParser(vertx))
                .body(Bodies.json(bodySchemaBuilder))
                .predicate(RequestPredicate.BODY_REQUIRED);

        }

        fun orderCancelPayloadValidator(vertx : Vertx): ValidationHandlerBuilder {

            val bodySchemaBuilder = objectSchema()
                .requiredProperty("orderId", stringSchema().with(minLength(36)).with(maxLength(36)))

            return ValidationHandlerBuilder
                .create(initSchemaParser(vertx))
                .body(Bodies.json(bodySchemaBuilder))
                .predicate(RequestPredicate.BODY_REQUIRED);

        }
    }
}