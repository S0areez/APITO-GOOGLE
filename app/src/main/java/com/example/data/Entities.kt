package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val email: String,
    val avatarUrl: String = "",
    val role: String, // "contratante" | "arbitro"
    val city: String = "São Paulo",
    val phone: String = "(11) 98765-4321",
    val bio: String = "",
    val modalities: String = "futebol,society", // comma separated
    val equipment: String = "Apito,Cartões,Cronômetro", // comma separated
    val hourlyRate: Double = 120.0,
    val level: String = "prata", // bronze, prata, ouro, black
    val gamesCount: Int = 12,
    val ratingAvg: Double = 4.8,
    val isVerified: Boolean = true,
    val availability: String = "Sexta,Sábado,Domingo",
    val certifications: String = "CBF,Federação Paulista",
    val contractorType: String = "Amador"
) {
    fun getModalitiesList(): List<String> =
        if (modalities.isBlank()) emptyList() else modalities.split(",").map { it.trim() }

    fun getEquipmentList(): List<String> =
        if (equipment.isBlank()) emptyList() else equipment.split(",").map { it.trim() }

    fun getAvailabilityList(): List<String> =
        if (availability.isBlank()) emptyList() else availability.split(",").map { it.trim() }
}

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val contractorId: String,
    val contractorName: String,
    val refereeId: String,
    val refereeName: String,
    val date: String, // e.g. "2026-10-15"
    val time: String, // e.g. "19:00"
    val location: String,
    val modality: String, // futebol, futsal, society, futebol_7
    val price: Double,
    val status: String = "pendente", // pendente, aceita, a_caminho, em_andamento, finalizada, cancelada
    val paymentMethod: String = "pix", // pix, cartao, saldo
    val contractorCheckin: Boolean = false,
    val refereeCheckin: Boolean = false,
    val duration: Int = 1, // hours
    val platformFee: Double = 15.0,
    val isSurge: Boolean = false,
    val abacateBillingId: String? = null,
    val abacateBillingUrl: String? = null,
    val abacateStatus: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "match_events")
data class MatchEventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val matchId: String,
    val type: String, // "gol", "cartao_amarelo", "cartao_vermelho", "falta"
    val minute: Int,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val matchId: String,
    val reviewerId: String,
    val reviewerName: String,
    val targetId: String,
    val rating: Float,
    val punctuality: Float = 5.0f,
    val professionalism: Float = 5.0f,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: String, // "entrada", "saida", "saque"
    val amount: Double,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)
