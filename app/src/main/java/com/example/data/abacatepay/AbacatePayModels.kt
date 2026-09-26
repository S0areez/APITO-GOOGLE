package com.example.data.abacatepay

data class AbacateProduct(
    val externalId: String,
    val name: String,
    val description: String,
    val quantity: Int = 1,
    val price: Long // em centavos (ex: R$ 150,00 = 15000)
)

data class AbacateCustomer(
    val name: String,
    val cellphone: String,
    val email: String,
    val taxId: String
)

data class CreateBillingRequest(
    val frequency: String = "ONE_TIME",
    val methods: List<String> = listOf("PIX"),
    val products: List<AbacateProduct>,
    val returnUrl: String = "https://apito-esportes.com/retorno",
    val completionUrl: String = "https://apito-esportes.com/sucesso",
    val customer: AbacateCustomer? = null,
    val externalId: String? = null
)

data class AbacateBillingData(
    val id: String,
    val url: String? = null,
    val amount: Long? = null,
    val status: String? = null, // PENDING, PAID, EXPIRED, REFUNDED, CANCELLED
    val devMode: Boolean? = null,
    val methods: List<String>? = null,
    val externalId: String? = null
)

data class CreateBillingResponse(
    val data: AbacateBillingData? = null,
    val error: String? = null
)

data class ListBillingResponse(
    val data: List<AbacateBillingData>? = null,
    val error: String? = null
)
