package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.ApitoViewModel
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoRed
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary

@Composable
fun IncomingMatchAlertOverlay(
    viewModel: ApitoViewModel,
    onNavigateToMatch: (String) -> Unit
) {
    val incomingOffer by viewModel.currentIncomingOffer.collectAsState()
    val countdown by viewModel.incomingOfferCountdown.collectAsState()
    val isAvailable by viewModel.isRefereeAvailable.collectAsState()

    if (incomingOffer != null && isAvailable) {
        val offer = incomingOffer!!

        Dialog(
            onDismissRequest = { viewModel.dismissCurrentIncomingOffer() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(24.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, ApitoGreen.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                        .testTag("incoming_match_modal"),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header with countdown and live sound indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ApitoGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NotificationsActive,
                                        contentDescription = null,
                                        tint = ApitoGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "CHAMADO UBER-DISPATCH",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ApitoGreen,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Nova partida disponível",
                                        fontSize = 12.sp,
                                        color = ApitoTextSecondary
                                    )
                                }
                            }

                            // Circular Countdown Timer
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { countdown / 30f },
                                    modifier = Modifier.size(38.dp),
                                    color = if (countdown > 10) ApitoGreen else ApitoRed,
                                    trackColor = ApitoSurfaceElevated,
                                    strokeWidth = 3.5.dp
                                )
                                Text(
                                    text = "$countdown",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Dynamic Price Display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            ApitoGreen.copy(alpha = 0.15f),
                                            ApitoGreen.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .border(1.5.dp, ApitoGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(vertical = 14.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "R$ ${String.format("%.2f", offer.price)}",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ApitoGreen
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.FlashOn, contentDescription = null, tint = ApitoGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Tarifa Dinâmica Inclusa",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ApitoGold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Location & Match Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ApitoBackground)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Stadium, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = offer.location,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ApitoTextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (offer.location.contains(",")) offer.location else "${offer.location} • São Paulo",
                                    fontSize = 12.sp,
                                    color = ApitoTextSecondary,
                                    lineHeight = 16.sp
                                )
                            }

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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.SportsSoccer, contentDescription = null, tint = ApitoCyan, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(offer.modality, fontSize = 11.sp, color = ApitoCyan, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ApitoSurfaceElevated)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = ApitoGold, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${offer.date} às ${offer.time}", fontSize = 11.sp, color = ApitoGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Actions: Accept / Decline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.dismissCurrentIncomingOffer() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("decline_match_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ApitoRed.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = ApitoRed, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pular", color = ApitoRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.acceptIncomingOffer { matchId ->
                                        onNavigateToMatch(matchId)
                                    }
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(52.dp)
                                    .testTag("accept_match_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ApitoGreen,
                                    contentColor = ApitoBackground
                                )
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ACEITAR PARTIDA", fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
