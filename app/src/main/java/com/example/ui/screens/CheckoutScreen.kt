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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoSurfaceVariant
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary
import java.util.Locale

@Composable
fun CheckoutScreen(
    refereeId: String,
    viewModel: ApitoViewModel,
    onBack: () -> Unit,
    onProceedToPayment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val referees by viewModel.referees.collectAsState()
    val referee = referees.firstOrNull { it.id == refereeId }

    var date by remember { mutableStateOf("2026-10-20") }
    var time by remember { mutableStateOf("20:00") }
    var location by remember { mutableStateOf("Arena Play Gol - Quadra 3, São Paulo") }
    var selectedModality by remember {
        mutableStateOf(referee?.getModalitiesList()?.firstOrNull() ?: "society")
    }
    var durationHours by remember { mutableIntStateOf(1) }
    var splitCount by remember { mutableIntStateOf(10) }
    var isSurge by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("abacatepay") }

    if (referee == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ApitoBackground),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = onBack) { Text("Voltar") }
        }
        return
    }

    val (totalPrice, platformFee, subtotal) = viewModel.calculatePrice(
        referee.hourlyRate,
        durationHours,
        isSurge
    )
    val pricePerPerson = if (splitCount > 1) totalPrice / splitCount else totalPrice

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Header
        Surface(color = ApitoSurface, tonalElevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ApitoTextPrimary
                    )
                }
                Text(
                    text = "Confirmar Contratação",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Referee Mini Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(ApitoGold.copy(alpha = 0.2f))
                                .border(1.5.dp, ApitoGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = referee.fullName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = ApitoGold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = referee.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = ApitoTextPrimary
                            )
                            Text(
                                text = "Árbitro ${referee.level.uppercase()} • ${referee.city}",
                                fontSize = 12.sp,
                                color = ApitoTextSecondary
                            )
                        }
                        Text(
                            text = "R$ ${referee.hourlyRate.toInt()}/h",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = ApitoGold
                        )
                    }
                }
            }

            // Match Details Form
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Dados da Partida",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ApitoTextPrimary
                        )

                        // Date & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                label = { Text("Data") },
                                leadingIcon = { Icon(Icons.Filled.CalendarMonth, null, tint = ApitoCyan, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f).testTag("input_match_date"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ApitoSurfaceElevated,
                                    unfocusedContainerColor = ApitoSurfaceElevated,
                                    focusedBorderColor = ApitoGold,
                                    unfocusedBorderColor = ApitoBorder,
                                    focusedTextColor = ApitoTextPrimary,
                                    unfocusedTextColor = ApitoTextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = time,
                                onValueChange = { time = it },
                                label = { Text("Horário") },
                                leadingIcon = { Icon(Icons.Filled.Schedule, null, tint = ApitoCyan, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f).testTag("input_match_time"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ApitoSurfaceElevated,
                                    unfocusedContainerColor = ApitoSurfaceElevated,
                                    focusedBorderColor = ApitoGold,
                                    unfocusedBorderColor = ApitoBorder,
                                    focusedTextColor = ApitoTextPrimary,
                                    unfocusedTextColor = ApitoTextPrimary
                                )
                            )
                        }

                        // Location
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Local / Arena") },
                            leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = ApitoGold, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().testTag("input_match_location"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ApitoSurfaceElevated,
                                unfocusedContainerColor = ApitoSurfaceElevated,
                                focusedBorderColor = ApitoGold,
                                unfocusedBorderColor = ApitoBorder,
                                focusedTextColor = ApitoTextPrimary,
                                unfocusedTextColor = ApitoTextPrimary
                            )
                        )

                        // Duration selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Duração da partida:", fontSize = 13.sp, color = ApitoTextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(1, 2, 3).forEach { d ->
                                    val isSelected = durationHours == d
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) ApitoGold else ApitoSurfaceElevated)
                                            .border(1.dp, if (isSelected) ApitoGold else ApitoBorder, RoundedCornerShape(8.dp))
                                            .clickable { durationHours = d }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${d}h",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) ApitoBackground else ApitoTextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Modality selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Modalidade:", fontSize = 13.sp, color = ApitoTextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("society", "futebol", "futsal").forEach { m ->
                                    val isSel = selectedModality == m
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) ApitoCyan else ApitoSurfaceElevated)
                                            .border(1.dp, if (isSel) ApitoCyan else ApitoBorder, RoundedCornerShape(8.dp))
                                            .clickable { selectedModality = m }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = m.uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSel) ApitoBackground else ApitoTextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Night/Peak surge toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Horário de Pico / Noturno (+20%)", fontSize = 12.sp, color = ApitoTextPrimary)
                                Text(text = "Taxa adicional de alta demanda", fontSize = 10.sp, color = ApitoTextMuted)
                            }
                            Switch(
                                checked = isSurge,
                                onCheckedChange = { isSurge = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ApitoGold,
                                    checkedTrackColor = ApitoGold.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }

            // Split Payment Calculator (Dividir com o time)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dividir com o Time",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { if (splitCount > 1) splitCount-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Menos", tint = ApitoGold)
                                }

                                Text(
                                    text = "$splitCount pessoas",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )

                                IconButton(
                                    onClick = { if (splitCount < 30) splitCount++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Mais", tint = ApitoGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ApitoGreen.copy(alpha = 0.15f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Valor por jogador:",
                                fontSize = 12.sp,
                                color = ApitoGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = String.format(Locale.US, "R$ %.2f", pricePerPerson),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = ApitoGreen
                            )
                        }
                    }
                }
            }

            // Payment Method Selector
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Forma de Pagamento",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ApitoTextPrimary
                        )

                        val paymentMethods = listOf(
                            Triple("abacatepay", "Abacate Pay 🥑 (PIX & Cartão Instantâneo)", Icons.Filled.QrCode),
                            Triple("cartao", "Cartão de Crédito", Icons.Filled.CreditCard),
                            Triple("saldo", "Saldo da Carteira", Icons.Filled.Wallet)
                        )

                        paymentMethods.forEach { (id, label, icon) ->
                            val isSel = paymentMethod == id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) ApitoSurfaceElevated else Color.Transparent)
                                    .border(1.dp, if (isSel) ApitoGold else ApitoBorder, RoundedCornerShape(12.dp))
                                    .clickable { paymentMethod = id }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSel) ApitoGold else ApitoTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = ApitoTextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSel) {
                                    Text(text = "●", color = ApitoGold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Summary Breakdown
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal arbitragem (${durationHours}h)", fontSize = 12.sp, color = ApitoTextSecondary)
                            Text("R$ ${subtotal.toInt()}", fontSize = 12.sp, color = ApitoTextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taxa de serviço e garantia (10%)", fontSize = 12.sp, color = ApitoTextSecondary)
                            Text("R$ ${platformFee.toInt()}", fontSize = 12.sp, color = ApitoTextPrimary)
                        }
                        if (isSurge) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Acréscimo horário nobre (+20%)", fontSize = 12.sp, color = ApitoGold)
                                Text("Incluso", fontSize = 12.sp, color = ApitoGold)
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ApitoBorder))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total da contratação", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ApitoTextPrimary)
                            Text("R$ ${totalPrice.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ApitoGold)
                        }
                    }
                }
            }
        }

        // Bottom CTA
        Surface(color = ApitoSurface, tonalElevation = 8.dp) {
            Button(
                onClick = {
                    viewModel.createBooking(
                        referee = referee,
                        date = date,
                        time = time,
                        location = location,
                        modality = selectedModality,
                        durationHours = durationHours,
                        paymentMethod = paymentMethod,
                        isSurge = isSurge,
                        onSuccess = { matchId ->
                            onProceedToPayment(matchId)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
                    .testTag("confirm_booking_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ApitoGold,
                    contentColor = ApitoBackground
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Avançar para Pagamento • R$ ${totalPrice.toInt()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
        }
    }
}
