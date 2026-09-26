package com.example.util

data class PricingBreakdown(
    val basePrice: Double,
    val timeSurcharge: Double,
    val timeLabel: String,
    val distanceCost: Double,
    val estimatedKm: Double,
    val surgeMultiplier: Double,
    val surgeLabel: String,
    val durationMultiplier: Double,
    val totalSuggestedPrice: Double
)

object DynamicPricingCalculator {

    fun calculate(
        modality: String,
        time: String, // "HH:mm" e.g. "21:30"
        estimatedKm: Double,
        durationHours: Double,
        isHighDemand: Boolean = false,
        isWeekend: Boolean = false
    ): PricingBreakdown {
        // 1. Base price by modality
        val base = when {
            modality.contains("futsal", ignoreCase = true) -> 100.0
            modality.contains("campo", ignoreCase = true) || modality.contains("11", ignoreCase = true) -> 150.0
            modality.contains("society", ignoreCase = true) || modality.contains("7", ignoreCase = true) -> 120.0
            else -> 120.0
        }

        // 2. Time-of-day surcharge (Uber/99 dynamic logic)
        // Quanto mais tarde, mais caro. Quanto mais cedo, mais barato.
        val hour = parseHour(time)
        val (timePercent, timeLabel) = when (hour) {
            in 0..5 -> Pair(0.50, "Madrugada (+50%)")
            in 6..17 -> Pair(0.00, "Diurno Padrão (Sem acréscimo)")
            in 18..21 -> Pair(0.20, "Pico Noturno (+20%)")
            in 22..23 -> Pair(0.35, "Noturno Tardio (+35%)")
            else -> Pair(0.15, "Horário Especial")
        }
        val timeSurcharge = base * timePercent

        // 3. Distance cost: R$ 2,50 per km
        val distanceCost = estimatedKm * 2.50

        // 4. Surge Demand multiplier
        val surgeMultiplier = when {
            isHighDemand && isWeekend -> 1.30
            isHighDemand -> 1.20
            isWeekend -> 1.15
            else -> 1.00
        }
        val surgeLabel = when {
            surgeMultiplier >= 1.30 -> "Super Alta Demanda (1.30x)"
            surgeMultiplier >= 1.20 -> "Alta Demanda (1.20x)"
            surgeMultiplier >= 1.15 -> "Fim de Semana (1.15x)"
            else -> "Tarifa Normal (1.0x)"
        }

        // 5. Duration multiplier
        val durationMultiplier = when {
            durationHours >= 2.0 -> 1.8
            durationHours >= 1.5 -> 1.4
            else -> 1.0
        }

        // Final calculation
        val subtotal = (base + timeSurcharge + distanceCost) * durationMultiplier
        val total = Math.round(subtotal * surgeMultiplier).toDouble()

        return PricingBreakdown(
            basePrice = base,
            timeSurcharge = timeSurcharge,
            timeLabel = timeLabel,
            distanceCost = distanceCost,
            estimatedKm = estimatedKm,
            surgeMultiplier = surgeMultiplier,
            surgeLabel = surgeLabel,
            durationMultiplier = durationMultiplier,
            totalSuggestedPrice = total.coerceAtLeast(100.0)
        )
    }

    private fun parseHour(timeStr: String): Int {
        return try {
            val clean = timeStr.trim().split(":").firstOrNull()
            clean?.toIntOrNull() ?: 20
        } catch (_: Exception) {
            20
        }
    }
}
