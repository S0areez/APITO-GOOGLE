package com.example.data.supabase

data class SupabaseProfile(
    val id: String,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val role: String? = "referee", // "referee", "arbitro", "organizer", "contratante"
    val city: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val rating: Double? = 0.0,
    val matches_completed: Int? = 0,
    val updated_at: String? = null
)

data class SupabaseMatch(
    val id: String? = null,
    val organizer_id: String? = null,
    val referee_id: String? = null,
    val location: String? = null,
    val address: String? = null,
    val category: String? = "Society 7",
    val price: Double? = 120.0,
    val status: String? = "aberta",
    val duration: Double? = 1.0,
    val match_date: String? = null,
    val level: String? = "Amador",
    val start_code: String? = "",
    val end_code: String? = "",
    val created_at: String? = null
)

data class SupabaseWallet(
    val id: String? = null,
    val user_id: String? = null,
    val balance: Double? = 0.0,
    val pending_balance: Double? = 0.0,
    val updated_at: String? = null
)

data class SupabaseRating(
    val id: String? = null,
    val match_id: String? = null,
    val reviewer_id: String? = null,
    val referee_id: String? = null,
    val rating: Float? = 5f,
    val comment: String? = null,
    val created_at: String? = null
)

data class SupabaseSignUpRequest(
    val email: String,
    val password: String,
    val data: Map<String, String>? = null
)

data class SupabaseSignInRequest(
    val email: String,
    val password: String
)

data class SupabaseUser(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val user_metadata: Map<String, Any?>? = null
)

data class SupabaseAuthResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val expires_in: Long? = null,
    val refresh_token: String? = null,
    val user: SupabaseUser? = null,
    val error_description: String? = null,
    val msg: String? = null,
    val message: String? = null
)
