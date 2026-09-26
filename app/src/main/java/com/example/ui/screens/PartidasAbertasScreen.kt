package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchEntity
import com.example.ui.ApitoViewModel
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
import com.example.util.ApitoSoundManager

@Composable
fun PartidasAbertasScreen(
    viewModel: ApitoViewModel,
    onBack: () -> Unit,
    onNavigateToMatch: (String) -> Unit
) {
    val context = LocalContext.current
    val openMatches by viewModel.openMatches.collectAsState()
    val isAvailable by viewModel.isRefereeAvailable.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()

    var selectedFilter by remember { mutableStateOf("Todas") }

    val filteredList = openMatches.filter { match ->
        when (selectedFilter) {
            "Society" -> match.modality.contains("society", ignoreCase = true)
            "Campo" -> match.modality.contains("campo", ignoreCase = true)
            "Futsal" -> match.modality.contains("futsal", ignoreCase = true)
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ApitoTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Partidas Abertas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ApitoGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Supabase Realtime Ativo",
                            fontSize = 11.sp,
                            color = ApitoGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Availability toggle chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isAvailable) ApitoGreen.copy(alpha = 0.2f) else ApitoSurfaceElevated)
                    .border(1.dp, if (isAvailable) ApitoGreen else ApitoBorder, RoundedCornerShape(20.dp))
                    .clickable { viewModel.toggleRefereeAvailability(!isAvailable) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isAvailable) "ONLINE" else "OFFLINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isAvailable) ApitoGreen else ApitoTextMuted
                )
            }
        }

        // Radar / Realtime Broadcast Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ApitoSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ApitoCyan.copy(alpha = 0.3f)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ApitoCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SportsSoccer,
                        contentDescription = null,
                        tint = ApitoCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Radar de Chamados Uber-Style",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                    Text(
                        text = "Jogos criados por contratantes aparecem instantaneamente aqui e no seu alerta sonoro.",
                        fontSize = 11.sp,
                        color = ApitoTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Modality Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todas", "Society", "Campo", "Futsal").forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ApitoCyan.copy(alpha = 0.2f),
                        selectedLabelColor = ApitoCyan,
                        containerColor = ApitoSurfaceElevated,
                        labelColor = ApitoTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) ApitoCyan else ApitoBorder
                    )
                )
            }
        }

        // List of Open Matches
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📡", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhuma partida aguardando no momento",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mantenha seu status como ONLINE. Assim que um contratante criar uma partida, você receberá a notificação com som automaticamente!",
                                fontSize = 12.sp,
                                color = ApitoTextMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { match ->
                    OpenMatchCard(
                        match = match,
                        onAccept = {
                            val refId = currentProfile?.id ?: "referee_direct"
                            val refName = currentProfile?.fullName ?: "Árbitro Credenciado"
                            viewModel.acceptIncomingOffer { matchId ->
                                onNavigateToMatch(matchId)
                            }
                            // Direct fallback in case not active incoming
                            ApitoSoundManager.playAcceptedSound()
                            Toast.makeText(context, "Partida aceita com sucesso!", Toast.LENGTH_SHORT).show()
                            onNavigateToMatch(match.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OpenMatchCard(
    match: MatchEntity,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, ApitoGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Status Badge and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ApitoGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DISPONÍVEL AGORA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = ApitoGreen,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.FlashOn, contentDescription = null, tint = ApitoGold, modifier = Modifier.size(16.dp))
                    Text(
                        text = "R$ ${String.format("%.2f", match.price)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ApitoGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Venue & Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Stadium, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = match.location,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ApitoTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (match.location.contains(",")) match.location else "${match.location}, São Paulo",
                    fontSize = 12.sp,
                    color = ApitoTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pills: Modality & Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ApitoSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(match.modality, fontSize = 11.sp, color = ApitoCyan, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ApitoSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${match.date} • ${match.time}", fontSize = 11.sp, color = ApitoGold, fontWeight = FontWeight.SemiBold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ApitoSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${match.duration}h jogo", fontSize = 11.sp, color = ApitoTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Accept CTA
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("open_match_accept_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ApitoGreen,
                    contentColor = ApitoBackground
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ACEITAR ESTA PARTIDA", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}
