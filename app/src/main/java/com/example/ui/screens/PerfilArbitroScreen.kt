package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.components.LevelBadge
import com.example.ui.components.RatingStars
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilArbitroScreen(
    refereeId: String,
    viewModel: ApitoViewModel,
    onBack: () -> Unit,
    onNavigateToCheckout: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val referees by viewModel.referees.collectAsState()
    val referee = referees.firstOrNull { it.id == refereeId }
    val reviews by viewModel.getReviews(refereeId).collectAsState(initial = emptyList())

    if (referee == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ApitoBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Árbitro não encontrado", color = ApitoTextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Voltar") }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Top Bar
        Surface(color = ApitoSurface, tonalElevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ApitoTextPrimary
                    )
                }
                Text(
                    text = "Perfil do Árbitro",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextPrimary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ApitoGold.copy(alpha = 0.4f), ApitoCyan.copy(alpha = 0.4f))
                                )
                            )
                            .border(3.dp, ApitoGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = referee.fullName.take(2).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = ApitoGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = referee.fullName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = ApitoTextPrimary
                        )
                        if (referee.isVerified) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verificado",
                                tint = ApitoCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = ApitoTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = referee.city,
                            fontSize = 13.sp,
                            color = ApitoTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LevelBadge(level = referee.level)
                        RatingStars(rating = referee.ratingAvg)
                    }

                    if (referee.bio.isNotBlank()) {
                        Text(
                            text = referee.bio,
                            fontSize = 13.sp,
                            color = ApitoTextSecondary,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            // Stats Triple Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val stats = listOf(
                        Triple(Icons.Filled.SportsSoccer, "${referee.gamesCount}", "Partidas"),
                        Triple(Icons.Filled.Timer, "98%", "Pontualidade"),
                        Triple(Icons.Filled.CreditCard, "2.4", "Média Cartões")
                    )

                    stats.forEach { (icon, value, label) ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, ApitoBorder, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = ApitoGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = value,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ApitoTextPrimary
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = ApitoTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Modalities Section
            item {
                Column {
                    Text(
                        text = "Modalidades Atendidas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        referee.getModalitiesList().forEach { mod ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ApitoSurfaceElevated)
                                    .border(1.dp, ApitoBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "⚽ ${mod.replace("_", " ").uppercase()}",
                                    fontSize = 12.sp,
                                    color = ApitoTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Equipment Section
            item {
                Column {
                    Text(
                        text = "Equipamentos Próprios",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        referee.getEquipmentList().forEach { eq ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ApitoCyan.copy(alpha = 0.12f))
                                    .border(1.dp, ApitoCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "✓ $eq",
                                    fontSize = 12.sp,
                                    color = ApitoCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Availability Section
            item {
                Column {
                    Text(
                        text = "Disponibilidade Típica",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        referee.getAvailabilityList().forEach { day ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ApitoGreen.copy(alpha = 0.12f))
                                    .border(1.dp, ApitoGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "📅 $day",
                                    fontSize = 12.sp,
                                    color = ApitoGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Reviews Section
            item {
                Text(
                    text = "Avaliações Recentes (${reviews.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = "Ainda não possui avaliações cadastradas.",
                        fontSize = 12.sp,
                        color = ApitoTextMuted
                    )
                }
            } else {
                items(reviews) { rev ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ApitoBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rev.reviewerName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                                RatingStars(rating = rev.rating.toDouble(), size = 12.dp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rev.comment,
                                fontSize = 12.sp,
                                color = ApitoTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Sticky Bottom CTA Bar
        Surface(
            color = ApitoSurface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Valor / jogo",
                        fontSize = 11.sp,
                        color = ApitoTextMuted
                    )
                    Text(
                        text = "R$ ${referee.hourlyRate.toInt()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = ApitoGold
                    )
                }

                Button(
                    onClick = { onNavigateToCheckout(referee.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ApitoGold,
                        contentColor = ApitoBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("hire_referee_cta_button")
                ) {
                    Text(
                        text = "Contratar Árbitro",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
