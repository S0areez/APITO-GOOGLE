package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.components.ApitoHeader
import com.example.ui.components.IncomingMatchAlertOverlay
import com.example.ui.components.MatchItemCard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
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

@Composable
fun ArbitroDashboardScreen(
    viewModel: ApitoViewModel,
    onNavigateToSolicitacoes: () -> Unit,
    onNavigateToMatch: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToOpenMatches: () -> Unit,
    onRoleSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val allMatches by viewModel.allMatches.collectAsState()
    val openMatches by viewModel.openMatches.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val isAvailable by viewModel.isRefereeAvailable.collectAsState()

    val myMatches = allMatches.filter {
        it.refereeId == currentProfile?.id || currentProfile?.role == "arbitro"
    }

    val pendingRequests = myMatches.filter { it.status == "pendente" }
    val upcomingMatches = myMatches.filter { it.status in listOf("aceita", "a_caminho", "em_andamento") }
        .sortedBy { it.date }
    val nextMatch = upcomingMatches.firstOrNull()

    // Earnings calculation
    val paidEarnings = transactions.filter { it.type == "entrada" }.sumOf { it.amount }
    val pendingEarnings = upcomingMatches.sumOf { it.price - it.platformFee }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        ApitoHeader(
            title = "Painel do Árbitro",
            subtitle = "Olá, ${currentProfile?.fullName ?: "Árbitro"}",
            currentRole = "arbitro",
            onRoleSwitch = onRoleSwitch
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Referee Availability Switch Card (Uber-style Online/Offline)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            1.5.dp,
                            if (isAvailable) ApitoGreen.copy(alpha = 0.6f) else ApitoBorder,
                            RoundedCornerShape(18.dp)
                        )
                        .testTag("availability_card"),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (isAvailable) ApitoGreen else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isAvailable) "VOCÊ ESTÁ ONLINE" else "VOCÊ ESTÁ OFFLINE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isAvailable) ApitoGreen else ApitoTextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = if (isAvailable) "Recebendo chamados instantâneos" else "Chamados pausados no momento",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ApitoTextPrimary
                                    )
                                }
                            }

                            Switch(
                                checked = isAvailable,
                                onCheckedChange = { viewModel.toggleRefereeAvailability(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ApitoGreen,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = ApitoSurfaceElevated
                                ),
                                modifier = Modifier.testTag("referee_availability_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAvailable)
                                "Novos jogos criados por contratantes tocarão um som na tela estilo Uber com opção de aceitar ou pular."
                            else
                                "Ative para receber partidas em tempo real criadas por organizadores.",
                            fontSize = 11.sp,
                            color = ApitoTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Real-Time Open Matches Radar Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onNavigateToOpenMatches)
                        .testTag("open_matches_radar_banner"),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ApitoCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Radar, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Partidas Abertas",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ApitoTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ApitoGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("${openMatches.size} ao vivo", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ApitoGreen)
                                    }
                                }
                                Text(
                                    text = "Ver jogos disponíveis no Supabase Realtime",
                                    fontSize = 11.sp,
                                    color = ApitoTextSecondary
                                )
                            }
                        }

                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }
            // Weekly Earnings Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("earnings_summary_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF047857), Color(0xFF0D9488))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Filled.TrendingUp, null, tint = ApitoGold, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Ganhos em Jogos",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable(onClick = onNavigateToWallet)
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Wallet, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ver Extrato", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "R$ ${paidEarnings.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Text(text = "Saldo liberado", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "R$ ${pendingEarnings.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ApitoGold)
                                    Text(text = "A receber (confirmados)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }

            // Availability Toggle Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
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
                                text = if (isAvailable) "Disponível para Partidas" else "Indisponível no Momento",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAvailable) ApitoGreen else ApitoTextSecondary
                            )
                            Text(
                                text = if (isAvailable) "Você está visível na busca de organizadores" else "Você não receberá novas solicitações",
                                fontSize = 11.sp,
                                color = ApitoTextMuted
                            )
                        }

                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { viewModel.toggleRefereeAvailability(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ApitoGreen,
                                checkedTrackColor = ApitoGreen.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }

            // Pending Requests Notification Banner
            if (pendingRequests.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ApitoGold.copy(alpha = 0.15f))
                            .border(1.5.dp, ApitoGold, RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToSolicitacoes)
                            .padding(16.dp)
                            .testTag("pending_requests_banner"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(ApitoGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Notifications, null, tint = ApitoBackground, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "${pendingRequests.size} Nova(s) Solicitação(ões)!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ApitoGold
                                )
                                Text(
                                    text = "Toque para aceitar ou recusar partidas",
                                    fontSize = 11.sp,
                                    color = ApitoTextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ApitoGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Next Match Highlight
            if (nextMatch != null) {
                item {
                    Text(
                        text = "Próximo Jogo a Apitar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                }

                item {
                    MatchItemCard(
                        match = nextMatch,
                        userRole = "arbitro",
                        onClick = { onNavigateToMatch(nextMatch.id) }
                    )
                }
            }

            // Upcoming Schedule list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Escala de Jogos (${upcomingMatches.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                }
            }

            if (upcomingMatches.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma partida agendada. Ative sua disponibilidade para receber convites!",
                        fontSize = 12.sp,
                        color = ApitoTextMuted
                    )
                }
            } else {
                items(upcomingMatches) { match ->
                    MatchItemCard(
                        match = match,
                        userRole = "arbitro",
                        onClick = { onNavigateToMatch(match.id) }
                    )
                }
            }
        }

        // Uber-style Real-Time Incoming Match Overlay with Sound
        IncomingMatchAlertOverlay(
            viewModel = viewModel,
            onNavigateToMatch = onNavigateToMatch
        )
    }
}
