package com.example.data.supabase

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @GET("rest/v1/profiles")
    suspend fun getProfiles(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseProfile>>

    @GET("rest/v1/profiles")
    suspend fun getProfileById(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("id") idFilter: String, // eq.{id}
        @Query("select") select: String = "*"
    ): Response<List<SupabaseProfile>>

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body request: SupabaseSignUpRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Query("grant_type") grantType: String = "password",
        @Body request: SupabaseSignInRequest
    ): Response<SupabaseAuthResponse>

    @GET("rest/v1/matches")
    suspend fun getMatches(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): Response<List<SupabaseMatch>>

    @POST("rest/v1/matches")
    suspend fun createMatch(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body match: SupabaseMatch
    ): Response<List<SupabaseMatch>>

    @PATCH("rest/v1/matches")
    suspend fun updateMatch(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("id") idFilter: String, // eq.{id}
        @Body updates: Map<String, String>
    ): Response<Unit>

    @GET("rest/v1/wallets")
    suspend fun getWallets(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("select") select: String = "*"
    ): Response<List<SupabaseWallet>>

    @GET("rest/v1/ratings")
    suspend fun getRatings(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): Response<List<SupabaseRating>>

    @POST("rest/v1/ratings")
    suspend fun createRating(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body rating: SupabaseRating
    ): Response<List<SupabaseRating>>
}
