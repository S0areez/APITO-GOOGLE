package com.example.data.supabase

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Classe utilitária para inicializar e gerenciar a conexão com o Supabase.
 * Integra tanto o SDK oficial supabase-kt (Auth, Postgrest, Realtime) quanto
 * endpoints REST diretos via Retrofit.
 */
class SupabaseClient {

    companion object {
        private const val TAG = "SupabaseClient"
        const val DEFAULT_SUPABASE_URL = "https://loueswohgxxhstqjmvgp.supabase.co/"

        @Volatile
        private var instance: SupabaseClient? = null

        fun getInstance(): SupabaseClient {
            return instance ?: synchronized(this) {
                instance ?: SupabaseClient().also { instance = it }
            }
        }
    }

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val supabaseUrl: String = resolveSupabaseUrl()
    val supabaseAnonKey: String = resolveSupabaseAnonKey()

    val supabaseKt: io.github.jan.supabase.SupabaseClient = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseAnonKey
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }

    private val api: SupabaseApi = Retrofit.Builder()
        .baseUrl(if (supabaseUrl.endsWith("/")) supabaseUrl else "$supabaseUrl/")
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(SupabaseApi::class.java)

    private fun resolveSupabaseUrl(): String {
        return try {
            val url = BuildConfig.SUPABASE_URL.trim()
            if (url.isNotBlank() && !url.contains("YOUR_")) {
                if (url.endsWith("/")) url else "$url/"
            } else {
                DEFAULT_SUPABASE_URL
            }
        } catch (e: Exception) {
            DEFAULT_SUPABASE_URL
        }
    }

    private fun resolveSupabaseAnonKey(): String {
        return try {
            val key = BuildConfig.SUPABASE_ANON_KEY.trim()
            if (key.isNotBlank() && !key.contains("YOUR_")) {
                key
            } else {
                // Chave anon fornecida no .env
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxvdWVzd29oZ3h4aHN0cWptdmdwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY2NzE3MDgsImV4cCI6MjA5MjI0NzcwOH0.2AlOwYpN6YvfOPDeGCt2g8qzqkGxc-1EeoDTRGl3-mI"
            }
        } catch (e: Exception) {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxvdWVzd29oZ3h4aHN0cWptdmdwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY2NzE3MDgsImV4cCI6MjA5MjI0NzcwOH0.2AlOwYpN6YvfOPDeGCt2g8qzqkGxc-1EeoDTRGl3-mI"
        }
    }

    fun hasConfiguredKey(): Boolean {
        return supabaseAnonKey.isNotBlank() && !supabaseAnonKey.contains("YOUR_")
    }

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        role: String,
        phone: String = "",
        city: String = ""
    ): Result<SupabaseAuthResponse> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val metadata = mutableMapOf(
                "full_name" to fullName,
                "role" to role
            )
            if (phone.isNotBlank()) metadata["phone"] = phone
            if (city.isNotBlank()) metadata["city"] = city

            val response = api.signUp(
                apiKey = supabaseAnonKey,
                request = SupabaseSignUpRequest(
                    email = email.trim(),
                    password = password,
                    data = metadata
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.user != null) {
                    Result.success(body)
                } else if (!body.error_description.isNullOrBlank()) {
                    Result.failure(Exception(body.error_description))
                } else if (!body.message.isNullOrBlank()) {
                    Result.failure(Exception(body.message))
                } else {
                    Result.success(body)
                }
            } else {
                val errorStr = response.errorBody()?.string() ?: "Erro no cadastro (${response.code()})"
                Log.w(TAG, "Erro cadastro Supabase: $errorStr")
                Result.failure(Exception(extractErrorMessage(errorStr)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção no cadastro Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<SupabaseAuthResponse> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.signIn(
                apiKey = supabaseAnonKey,
                request = SupabaseSignInRequest(email = email.trim(), password = password)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.user != null) {
                    Result.success(body)
                } else if (!body.error_description.isNullOrBlank()) {
                    Result.failure(Exception(body.error_description))
                } else if (!body.message.isNullOrBlank()) {
                    Result.failure(Exception(body.message))
                } else {
                    Result.success(body)
                }
            } else {
                val errorStr = response.errorBody()?.string() ?: "Credenciais inválidas"
                Log.w(TAG, "Erro login Supabase: $errorStr")
                Result.failure(Exception(extractErrorMessage(errorStr)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção login Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun fetchProfileById(userId: String): Result<SupabaseProfile?> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada."))
        }
        return try {
            val response = api.getProfileById(
                apiKey = supabaseAnonKey,
                auth = "Bearer $supabaseAnonKey",
                idFilter = "eq.$userId"
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.firstOrNull())
            } else {
                val error = response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(rawJson: String): String {
        return try {
            if (rawJson.contains("\"error_description\":\"")) {
                rawJson.substringAfter("\"error_description\":\"").substringBefore("\"")
            } else if (rawJson.contains("\"msg\":\"")) {
                rawJson.substringAfter("\"msg\":\"").substringBefore("\"")
            } else if (rawJson.contains("\"message\":\"")) {
                val msg = rawJson.substringAfter("\"message\":\"").substringBefore("\"")
                if (msg == "Invalid login credentials") "Email ou senha incorretos." else msg
            } else {
                rawJson
            }
        } catch (e: Exception) {
            rawJson
        }
    }

    suspend fun fetchProfiles(): Result<List<SupabaseProfile>> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.getProfiles(apiKey = supabaseAnonKey, auth = "Bearer $supabaseAnonKey")
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Sucesso buscando ${response.body()!!.size} perfis do Supabase")
                Result.success(response.body()!!)
            } else {
                val error = response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Log.w(TAG, "Erro buscando perfis: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao buscar perfis no Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun fetchMatches(): Result<List<SupabaseMatch>> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.getMatches(apiKey = supabaseAnonKey, auth = "Bearer $supabaseAnonKey")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Log.w(TAG, "Erro buscando partidas: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao buscar partidas no Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun createMatch(match: SupabaseMatch): Result<SupabaseMatch?> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.createMatch(apiKey = supabaseAnonKey, auth = "Bearer $supabaseAnonKey", match = match)
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                Result.success(response.body()!!.first())
            } else {
                val error = response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Log.w(TAG, "Erro criando partida no Supabase: $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao criar partida no Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun updateMatchStatus(matchId: String, status: String): Result<Boolean> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.updateMatch(
                apiKey = supabaseAnonKey,
                auth = "Bearer $supabaseAnonKey",
                idFilter = "eq.$matchId",
                updates = mapOf("status" to status)
            )
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Log.e(TAG, "Exceção atualizando partida no Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun updateMatch(id: String, updates: Map<String, String>): Result<Boolean> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.updateMatch(
                apiKey = supabaseAnonKey,
                auth = "Bearer $supabaseAnonKey",
                idFilter = "eq.$id",
                updates = updates
            )
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Log.e(TAG, "Exceção atualizando partida no Supabase", e)
            Result.failure(e)
        }
    }

    suspend fun fetchWallets(): Result<List<SupabaseWallet>> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.getWallets(apiKey = supabaseAnonKey, auth = "Bearer $supabaseAnonKey")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchRatings(): Result<List<SupabaseRating>> {
        if (!hasConfiguredKey()) {
            return Result.failure(IllegalStateException("SUPABASE_ANON_KEY não configurada no .env."))
        }
        return try {
            val response = api.getRatings(apiKey = supabaseAnonKey, auth = "Bearer $supabaseAnonKey")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val error = response.errorBody()?.string() ?: "Erro HTTP ${response.code()}"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
