package com.example.data.abacatepay

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AbacatePayApi {

    @POST("v1/billing/create")
    suspend fun createBilling(
        @Header("Authorization") authHeader: String,
        @Body request: CreateBillingRequest
    ): Response<CreateBillingResponse>

    @GET("v1/billing/list")
    suspend fun listBillings(
        @Header("Authorization") authHeader: String
    ): Response<ListBillingResponse>
}
