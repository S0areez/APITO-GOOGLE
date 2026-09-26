package com.example

import com.example.data.abacatepay.AbacateProduct
import com.example.data.abacatepay.CreateBillingRequest
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAbacateBillingAmountConversion() {
    val amountReais = 150.0
    val amountCents = (amountReais * 100).toLong()
    assertEquals(15000L, amountCents)

    val product = AbacateProduct(
      externalId = "match_1",
      name = "Arbitragem Futebol Society",
      description = "Apito Match",
      quantity = 1,
      price = amountCents
    )

    assertEquals(15000L, product.price)

    val request = CreateBillingRequest(
      frequency = "ONE_TIME",
      methods = listOf("PIX"),
      products = listOf(product)
    )

    assertEquals("ONE_TIME", request.frequency)
    assertEquals(listOf("PIX"), request.methods)
    assertEquals(1, request.products.size)
  }
}

