package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary
import com.example.util.DynamicPricingCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPartidaModal(
    viewModel: ApitoViewModel,
    onDismiss: () -> Unit,
    onCreated: (String) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var locationName by remember { mutableStateOf("Arena Morumbi") }
    var address by remember { mutableStateOf("Av. Giovanni Gronchi, 1000 - Morumbi, SP") }
    var selectedModality by remember { mutableStateOf("Society 7") }
    var selectedDate by remember { mutableStateOf("Hoje") }
    var selectedTime by remember { mutableStateOf("21:00") }
    var estimatedDistanceKm by remember { mutableDoubleStateOf(8.0) }
    var durationHours by remember { mutableDoubleStateOf(1.0) }
    var matchLevel by remember { mutableStateOf("Amador") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Dynamic Pricing (Uber / 99 calculation)
    val pricingBreakdown by remember(selectedModality, selectedTime, estimatedDistanceKm, durationHours) {
        derivedStateOf {
            DynamicPricingCalculator.calculate(
                modality = selectedModality,
                time = selectedTime,
                estimatedKm = estimatedDistanceKm,
                durationHours = durationHours,
                isHighDemand = selectedTime.startsWith("20") || selectedTime.startsWith("21") || selectedTime.startsWith("22"),
                isWeekend = selectedDate.contains("Sábado", ignoreCase = true) || selectedDate.contains("Domingo", ignoreCase = true)
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ApitoSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ApitoGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SportsSoccer,
                            contentDescription = null,
                            tint = ApitoGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Novo Chamado (Estilo Uber)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ApitoTextPrimary
                        )
                        Text(
                            text = "Tarifa dinâmica e envio instantâneo",
                            fontSize = 12.sp,
                            color = ApitoTextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Fechar",
                        tint = ApitoTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field 1: Local / Arena
            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Nome da Arena / Clube") },
                placeholder = { Text("Ex: Arena Morumbi, Playball Pompeia") },
                leadingIcon = {
                    Icon(Icons.Filled.Stadium, contentDescription = null, tint = ApitoCyan)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("match_location_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ApitoCyan,
                    unfocusedBorderColor = ApitoBorder,
                    focusedContainerColor = ApitoBackground,
                    unfocusedContainerColor = ApitoBackground,
                    focusedTextColor = ApitoTextPrimary,
                    unfocusedTextColor = ApitoTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Field 2: Endereço Completo
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Endereço Completo") },
                placeholder = { Text("Rua, Número, Bairro, Cidade") },
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ApitoGold)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("match_address_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ApitoCyan,
                    unfocusedBorderColor = ApitoBorder,
                    focusedContainerColor = ApitoBackground,
                    unfocusedContainerColor = ApitoBackground,
                    focusedTextColor = ApitoTextPrimary,
                    unfocusedTextColor = ApitoTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Modalidade / Categoria Chips
            Text(
                text = "Modalidade do Jogo",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ApitoTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Society 7", "Campo 11", "Futsal").forEach { mod ->
                    val isSelected = selectedModality == mod
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedModality = mod },
                        label = { Text(mod, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Data e Horário
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Data", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Hoje", "Amanhã", "Sábado").forEach { d ->
                            val isSel = selectedDate == d
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ApitoGreen.copy(alpha = 0.25f) else ApitoSurfaceElevated)
                                    .border(1.dp, if (isSel) ApitoGreen else ApitoBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedDate = d }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(d, fontSize = 11.sp, color = if (isSel) ApitoGreen else ApitoTextSecondary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Horário de Início", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("19:00", "20:30", "22:00").forEach { t ->
                            val isSel = selectedTime == t
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ApitoCyan.copy(alpha = 0.25f) else ApitoSurfaceElevated)
                                    .border(1.dp, if (isSel) ApitoCyan else ApitoBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedTime = t }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(t, fontSize = 11.sp, color = if (isSel) ApitoCyan else ApitoTextSecondary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Distância Estimada (Slider)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DirectionsCar, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Deslocamento Estimado", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoTextPrimary)
                }
                Text(
                    text = "${estimatedDistanceKm.toInt()} km (+R$ ${String.format("%.2f", estimatedDistanceKm * 2.5)})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoCyan
                )
            }
            Slider(
                value = estimatedDistanceKm.toFloat(),
                onValueChange = { estimatedDistanceKm = it.toDouble() },
                valueRange = 2f..35f,
                steps = 6,
                colors = SliderDefaults.colors(
                    thumbColor = ApitoCyan,
                    activeTrackColor = ApitoCyan,
                    inactiveTrackColor = ApitoSurfaceElevated
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Duração do Jogo
            Text("Duração da Partida", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Pair(1.0, "1 hora"), Pair(1.5, "1h 30min"), Pair(2.0, "2 horas")).forEach { (dur, label) ->
                    val isSel = durationHours == dur
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ApitoGold.copy(alpha = 0.2f) else ApitoSurfaceElevated)
                            .border(1.dp, if (isSel) ApitoGold else ApitoBorder, RoundedCornerShape(8.dp))
                            .clickable { durationHours = dur }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(label, fontSize = 12.sp, color = if (isSel) ApitoGold else ApitoTextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // UBER / 99 DYNAMIC PRICING CARD BREAKDOWN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ApitoBackground),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ApitoGold.copy(alpha = 0.4f)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.FlashOn, contentDescription = null, tint = ApitoGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tarifa Dinâmica Calculada", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoGold)
                        }
                        Text(pricingBreakdown.surgeLabel, fontSize = 11.sp, color = ApitoGreen, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Line Items
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tarifa Base (${selectedModality})", fontSize = 12.sp, color = ApitoTextSecondary)
                        Text("R$ ${String.format("%.2f", pricingBreakdown.basePrice)}", fontSize = 12.sp, color = ApitoTextPrimary)
                    }

                    if (pricingBreakdown.timeSurcharge > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Horário: ${pricingBreakdown.timeLabel}", fontSize = 12.sp, color = ApitoTextSecondary)
                            Text("+ R$ ${String.format("%.2f", pricingBreakdown.timeSurcharge)}", fontSize = 12.sp, color = ApitoCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Deslocamento (${estimatedDistanceKm.toInt()} km)", fontSize = 12.sp, color = ApitoTextSecondary)
                        Text("+ R$ ${String.format("%.2f", pricingBreakdown.distanceCost)}", fontSize = 12.sp, color = ApitoCyan)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = ApitoBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Valor Total ao Árbitro", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoTextPrimary)
                            Text("Sem taxas ocultas, tudo incluso", fontSize = 11.sp, color = ApitoTextMuted)
                        }
                        Text(
                            text = "R$ ${String.format("%.2f", pricingBreakdown.totalSuggestedPrice)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = ApitoGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CTA Submit Button
            Button(
                onClick = {
                    if (locationName.isBlank() || address.isBlank()) {
                        Toast.makeText(context, "Preencha o local e o endereço da partida.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSubmitting = true
                    viewModel.publishMatch(
                        location = locationName,
                        address = address,
                        date = selectedDate,
                        time = selectedTime,
                        modality = selectedModality,
                        price = pricingBreakdown.totalSuggestedPrice,
                        durationHours = durationHours,
                        level = matchLevel,
                        onSuccess = { match ->
                            isSubmitting = false
                            Toast.makeText(context, "Chamado enviado em tempo real para os árbitros!", Toast.LENGTH_LONG).show()
                            onCreated(match.id)
                        },
                        onError = { err ->
                            isSubmitting = false
                            Toast.makeText(context, "Erro: $err", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_match_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ApitoGreen,
                    contentColor = ApitoBackground
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = ApitoBackground, strokeWidth = 2.5.dp)
                } else {
                    Icon(Icons.Filled.SportsSoccer, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Disparar Chamado (R$ ${String.format("%.0f", pricingBreakdown.totalSuggestedPrice)})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
