package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.components.RatingStars
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBlue
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoRed
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoSurfaceVariant
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary
import java.util.Locale

@Composable
fun ModoPartidaScreen(
    matchId: String,
    viewModel: ApitoViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentProfile by viewModel.currentProfile.collectAsState()
    val activeMatch by viewModel.activeMatch.collectAsState()
    val events by viewModel.activeMatchEvents.collectAsState()
    val timerSeconds by viewModel.matchTimerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val shareGps by viewModel.shareGps.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.loadMatchDetail(matchId)
    }

    val match = activeMatch
    val isReferee = currentProfile?.role == "arbitro"
    val isContractor = currentProfile?.role == "contratante"

    // Dialog state for adding occurrences
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedEventType by remember { mutableStateOf("gol") }
    var eventMinute by remember { mutableIntStateOf(1) }
    var eventDescription by remember { mutableStateOf("") }

    // Dialog state for post-match review
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableFloatStateOf(5f) }
    var reviewPunctuality by remember { mutableFloatStateOf(5f) }
    var reviewProfessionalism by remember { mutableFloatStateOf(5f) }
    var reviewComment by remember { mutableStateOf("") }

    val formattedTimer = remember(timerSeconds) {
        val mins = timerSeconds / 60
        val secs = timerSeconds % 60
        String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    val isOvertime = timerSeconds > 3600 // 60 min elapsed

    if (match == null) {
        Box(
            modifier = modifier.fillMaxSize().background(ApitoBackground),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = onBack) { Text("Voltar") }
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
                    Text(
                        text = "Modo Partida",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                }

                StatusBadge(status = match.status, modifier = Modifier.padding(end = 12.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Match Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${match.modality.replace("_", " ").uppercase()} • ${match.duration}H",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoGold
                            )
                            Text(
                                text = "R$ ${match.price.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ApitoTextPrimary
                            )
                        }

                        Text(
                            text = "📍 ${match.location}",
                            fontSize = 12.sp,
                            color = ApitoTextSecondary
                        )

                        Text(
                            text = "📅 ${match.date} às ${match.time}",
                            fontSize = 12.sp,
                            color = ApitoTextMuted
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isReferee) "Contratante: ${match.contractorName}" else "Árbitro: ${match.refereeName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ApitoTextPrimary
                            )

                            // Contact Action
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ApitoSurfaceElevated)
                                    .clickable {
                                        Toast.makeText(context, "Ligando para o organizador...", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.Call, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(14.dp))
                                Text(text = "Contato", fontSize = 11.sp, color = ApitoCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Double Check-in Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Check-in Duplo no Local",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ApitoTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Contractor Check-in
                            val cChecked = match.contractorCheckin
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (cChecked) ApitoGreen.copy(alpha = 0.15f) else ApitoSurfaceElevated)
                                    .border(1.dp, if (cChecked) ApitoGreen else ApitoBorder, RoundedCornerShape(12.dp))
                                    .clickable(enabled = isContractor) {
                                        viewModel.toggleContractorCheckin(matchId)
                                    }
                                    .padding(12.dp)
                                    .testTag("contractor_checkin_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = if (cChecked) ApitoGreen else ApitoTextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Contratante",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (cChecked) ApitoGreen else ApitoTextSecondary
                                    )
                                    Text(
                                        text = if (cChecked) "Confirmado ✓" else "Pendente",
                                        fontSize = 10.sp,
                                        color = if (cChecked) ApitoGreen else ApitoTextMuted
                                    )
                                }
                            }

                            // Referee Check-in
                            val rChecked = match.refereeCheckin
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (rChecked) ApitoGreen.copy(alpha = 0.15f) else ApitoSurfaceElevated)
                                    .border(1.dp, if (rChecked) ApitoGreen else ApitoBorder, RoundedCornerShape(12.dp))
                                    .clickable(enabled = isReferee) {
                                        viewModel.toggleRefereeCheckin(matchId)
                                    }
                                    .padding(12.dp)
                                    .testTag("referee_checkin_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = if (rChecked) ApitoGreen else ApitoTextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Árbitro",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rChecked) ApitoGreen else ApitoTextSecondary
                                    )
                                    Text(
                                        text = if (rChecked) "Confirmado ✓" else "Pendente",
                                        fontSize = 10.sp,
                                        color = if (rChecked) ApitoGreen else ApitoTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Live Match Stopwatch & Overtime
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, if (isOvertime) ApitoRed else ApitoGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cronômetro do Jogo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextSecondary
                            )

                            if (isTimerRunning) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.FiberManualRecord, null, tint = ApitoGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ao Vivo", color = ApitoGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Digital Clock
                        Text(
                            text = formattedTimer,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (isOvertime) ApitoRed else ApitoGold
                        )

                        if (isOvertime) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ApitoRed.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Warning, null, tint = ApitoRed, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Tempo Extra Detectado (+10m)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Timer Controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (!isTimerRunning) {
                                Button(
                                    onClick = {
                                        viewModel.updateMatchStatus(matchId, "em_andamento")
                                        viewModel.startTimer()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ApitoGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Iniciar Partida", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.stopTimer() },
                                    colors = ButtonDefaults.buttonColors(containerColor = ApitoRed),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.Stop, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pausar Tempo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.updateMatchStatus(matchId, "finalizada")
                                    showReviewDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ApitoGold, contentColor = ApitoBackground),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Apito Final", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Súmula Digital / Occurrences logger
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Súmula Digital ao Vivo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )

                    Button(
                        onClick = {
                            eventMinute = (timerSeconds / 60).toInt().coerceAtLeast(1)
                            showEventDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ApitoSurfaceElevated, contentColor = ApitoGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_event_button")
                    ) {
                        Text("+ Registrar Ocorrência", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (events.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum evento registrado ainda na súmula.",
                        fontSize = 12.sp,
                        color = ApitoTextMuted
                    )
                }
            } else {
                items(events) { ev ->
                    val (iconEmoji, badgeColor) = when (ev.type) {
                        "gol" -> Pair("⚽", ApitoGreen)
                        "cartao_amarelo" -> Pair("🟨", ApitoGold)
                        "cartao_vermelho" -> Pair("🟥", ApitoRed)
                        else -> Pair("⚠️", ApitoCyan)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ApitoBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ev.description,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                                Text(
                                    text = ev.type.replace("_", " ").uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = badgeColor
                                )
                            }
                            Text(
                                text = "${ev.minute}'",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = ApitoGold
                            )
                        }
                    }
                }
            }

            // GPS Telemetry simulator card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.Navigation, null, tint = ApitoCyan, modifier = Modifier.size(22.dp))
                            Column {
                                Text(
                                    text = if (shareGps) "GPS Ativo: No Local da Partida" else "GPS de Trajeto",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                                Text(
                                    text = "Telemetria de chegada do árbitro",
                                    fontSize = 11.sp,
                                    color = ApitoTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleGpsSharing() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (shareGps) ApitoGreen else ApitoSurface,
                                contentColor = if (shareGps) Color.White else ApitoCyan
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (shareGps) "Ao Vivo" else "Ativar GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to record occurrence
    if (showEventDialog) {
        AlertDialog(
            onDismissRequest = { showEventDialog = false },
            containerColor = ApitoSurface,
            title = {
                Text("Registrar na Súmula", color = ApitoTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair("gol", "⚽ Gol"),
                            Pair("cartao_amarelo", "🟨 Amarelo"),
                            Pair("cartao_vermelho", "🟥 Vermelho"),
                            Pair("falta", "⚠️ Falta")
                        ).forEach { (type, label) ->
                            val isSel = selectedEventType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ApitoGold else ApitoSurfaceElevated)
                                    .clickable { selectedEventType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) ApitoBackground else ApitoTextPrimary
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = eventMinute.toString(),
                        onValueChange = { eventMinute = it.toIntOrNull() ?: 0 },
                        label = { Text("Minuto do lance") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ApitoTextPrimary,
                            unfocusedTextColor = ApitoTextPrimary,
                            focusedBorderColor = ApitoGold,
                            unfocusedBorderColor = ApitoBorder
                        )
                    )

                    OutlinedTextField(
                        value = eventDescription,
                        onValueChange = { eventDescription = it },
                        label = { Text("Descrição / Jogador") },
                        placeholder = { Text("Ex: Gol de falta, camisa 9") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ApitoTextPrimary,
                            unfocusedTextColor = ApitoTextPrimary,
                            focusedBorderColor = ApitoGold,
                            unfocusedBorderColor = ApitoBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (eventDescription.isNotBlank()) {
                            viewModel.addMatchEvent(
                                matchId = matchId,
                                type = selectedEventType,
                                minute = eventMinute,
                                description = eventDescription
                            )
                            showEventDialog = false
                            eventDescription = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ApitoGold, contentColor = ApitoBackground)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEventDialog = false }) {
                    Text("Cancelar", color = ApitoTextMuted)
                }
            }
        )
    }

    // Modal Dialog for Post-Match Review
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            containerColor = ApitoSurface,
            title = {
                Text("Avaliar Partida", color = ApitoTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Como foi a atuação na partida?", fontSize = 12.sp, color = ApitoTextSecondary)

                    RatingStars(
                        rating = reviewRating.toDouble(),
                        size = 28.dp,
                        interactive = true,
                        onRatingChange = { reviewRating = it }
                    )

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Comentário ou feedback") },
                        placeholder = { Text("Pontualidade, respeito, condução do jogo...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ApitoTextPrimary,
                            unfocusedTextColor = ApitoTextPrimary,
                            focusedBorderColor = ApitoGold,
                            unfocusedBorderColor = ApitoBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val targetId = if (isReferee) match.contractorId else match.refereeId
                        viewModel.submitReview(
                            matchId = matchId,
                            targetId = targetId,
                            rating = reviewRating,
                            punctuality = reviewPunctuality,
                            professionalism = reviewProfessionalism,
                            comment = reviewComment.ifBlank { "Partida conduzida com excelência!" },
                            onDone = {
                                showReviewDialog = false
                                Toast.makeText(context, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ApitoGold, contentColor = ApitoBackground)
                ) {
                    Text("Enviar Avaliação")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Pular", color = ApitoTextMuted)
                }
            }
        )
    }
}
