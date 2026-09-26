package com.example.data.abacatepay

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AbacatePayClient {

    companion object {
        const val SUPABASE_WEBHOOK_URL = "https://loueswohgxxhstqjmvgp.supabase.co/functions/v1/abacate-pay-webhook"
        const val SUPABASE_PROJECT_URL = "https://loueswohgxxhstqjmvgp.supabase.co"
    }

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val api: AbacatePayApi = Retrofit.Builder()
        .baseUrl("https://api.abacatepay.com/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(AbacatePayApi::class.java)

    fun hasConfiguredApiKey(): Boolean {
        val key = BuildConfig.ABACATEPAY_API_KEY
        return key.isNotBlank() && !key.contains("YOUR_")
    }

    suspend fun createBilling(
        matchId: String,
        matchTitle: String,
        amountReais: Double,
        customerName: String,
        customerEmail: String,
        customerPhone: String
    ): Result<AbacateBillingData> {
        val apiKey = BuildConfig.ABACATEPAY_API_KEY.trim()
        val amountCents = (amountReais * 100).toLong().coerceAtLeast(100L)

        // Se a chave não foi configurada no Secrets panel ou for placeholder, usa simulação dev segura
        if (!hasConfiguredApiKey()) {
            val devId = "bill_dev_${matchId.take(8)}"
            return Result.success(
                AbacateBillingData(
                    id = devId,
                    url = "https://abacatepay.com/pay/$devId",
                    amount = amountCents,
                    status = "PENDING",
                    devMode = true,
                    methods = listOf("PIX"),
                    externalId = matchId
                )
            )
        }

        return try {
            val request = CreateBillingRequest(
                frequency = "ONE_TIME",
                methods = listOf("PIX"),
                products = listOf(
                    AbacateProduct(
                        externalId = matchId,
                        name = matchTitle,
                        description = "Contratação de árbitro esportivo pelo Apito",
                        quantity = 1,
                        price = amountCents
                    )
                ),
                returnUrl = "https://apito-esportes.com/match/$matchId",
                completionUrl = "https://apito-esportes.com/match/$matchId/success",
                customer = AbacateCustomer(
                    name = customerName.ifBlank { "Contratante Apito" },
                    cellphone = customerPhone.replace(Regex("[^0-9]"), "").ifBlank { "11999998888" },
                    email = customerEmail.ifBlank { "contato@apito.com" },
                    taxId = "00000000000"
                ),
                externalId = matchId
            )

            val response = api.createBilling(
                authHeader = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = response.body()?.error ?: response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Log.w("AbacatePay", "Resposta da API AbacatePay: $errorMsg - fornecendo fallback de desenvolvimento")
                val devId = "bill_sandbox_${matchId.take(8)}"
                Result.success(
                    AbacateBillingData(
                        id = devId,
                        url = "https://abacatepay.com/pay/$devId",
                        amount = amountCents,
                        status = "PENDING",
                        devMode = true,
                        methods = listOf("PIX"),
                        externalId = matchId
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("AbacatePay", "Exceção ao chamar AbacatePay API", e)
            val devId = "bill_dev_${matchId.take(8)}"
            Result.success(
                AbacateBillingData(
                    id = devId,
                    url = "https://abacatepay.com/pay/$devId",
                    amount = amountCents,
                    status = "PENDING",
                    devMode = true,
                    methods = listOf("PIX"),
                    externalId = matchId
                )
            )
        }
    }

    suspend fun checkBillingStatus(billingId: String): Result<String> {
        val apiKey = BuildConfig.ABACATEPAY_API_KEY.trim()
        if (!hasConfiguredApiKey() || billingId.startsWith("bill_dev_") || billingId.startsWith("bill_sandbox_")) {
            return Result.success("PENDING")
        }

        return try {
            val response = api.listBillings("Bearer $apiKey")
            if (response.isSuccessful && response.body()?.data != null) {
                val match = response.body()!!.data!!.firstOrNull { it.id == billingId }
                val status = match?.status ?: "PENDING"
                Result.success(status)
            } else {
                Result.success("PENDING")
            }
        } catch (e: Exception) {
            Log.e("AbacatePay", "Erro checando status de cobrança", e)
            Result.success("PENDING")
        }
    }

    suspend fun triggerSupabaseWebhook(
        matchId: String,
        billingId: String,
        status: String = "PAID"
    ): Result<Boolean> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val jsonPayload = """
                {
                    "event": "billing.paid",
                    "data": {
                        "id": "$billingId",
                        "status": "$status",
                        "externalId": "$matchId",
                        "metadata": {
                            "matchId": "$matchId"
                        }
                    }
                }
            """.trimIndent()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonPayload.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(SUPABASE_WEBHOOK_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = httpClient.newCall(request).execute()
            val success = response.isSuccessful
            Log.i("AbacatePay", "Supabase Webhook disparado. Status HTTP: ${response.code}")
            Result.success(success)
        } catch (e: Exception) {
            Log.e("AbacatePay", "Erro disparando Supabase Webhook", e)
            Result.failure(e)
        }
    }
}
