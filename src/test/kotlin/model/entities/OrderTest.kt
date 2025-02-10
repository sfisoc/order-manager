package model.entities

import model.enums.Side
import org.example.model.entities.Order
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.UUID

// todo Dummy test to remove later
class OrderTest {

@Test
 fun component2() {

 val id = UUID.randomUUID().toString();

 val value = Order(id,
     side = Side.BUY,
     quantity = 0.2587,
     price = "900",
     currencyPair = "BTCZAR",
     orderCount = 1,
 )

 assertEquals(id,value.id)

}

@Test
 fun copy() {}
}