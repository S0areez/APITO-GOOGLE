package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.components.MatchItemCard
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoRed
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary

@Composable
fun SolicitacoesScreen(
    viewModel: ApitoViewModel,
    onNavigateToMatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allMatches by viewModel.allMatches.collectAsState()

    val pending = allMatches.filter { it.status == "pendente" }
    val others = allMatches.filter { it.status != "pendente" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Header
        Surface(color = ApitoSurface, tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Solicitações de Partidas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary
                )
                Text(
                    text = "Aceite partidas para garantir sua escala e pagamento.",
                    fontSize = 12.sp,
                    color = ApitoTextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pending Requests section
            item {
                Text(
                    text = "Pendentes de Resposta (${pending.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoGold
                )
            }

            if (pending.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma solicitação pendente no momento.",
                        fontSize = 12.sp,
                        color = ApitoTextMuted
                    )
                }
            } else {
                items(pending) { match ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, ApitoGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .testTag("request_card_${match.id}"),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = match.modality.replace("_", " ").uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                                Text(
                                    text = "R$ ${match.price.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ApitoGold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Person, null, tint = ApitoCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Organizador: ${match.contractorName}",
                                    fontSize = 12.sp,
                                    color = ApitoTextSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CalendarMonth, null, tint = ApitoCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${match.date} às ${match.time} (${match.duration}h)",
                                    fontSize = 12.sp,
                                    color = ApitoTextSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocationOn, null, tint = ApitoRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = match.location,
                                    fontSize = 12.sp,
                                    color = ApitoTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Accept / Reject Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.rejectMatch(match.id)
                                        Toast.makeText(context, "Solicitação recusada.", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.Close, null, tint = ApitoRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Recusar", color = ApitoRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.acceptMatch(match.id)
                                        Toast.makeText(context, "Partida aceita! Adicionada à sua escala.", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f).testTag("accept_match_button_${match.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = ApitoGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Aceitar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Other matches
            item {
                Text(
                    text = "Histórico de Solicitações",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(others) { match ->
                MatchItemCard(
                    match = match,
                    userRole = "arbitro",
                    onClick = { onNavigateToMatch(match.id) }
                )
            }
        }
    }
}
