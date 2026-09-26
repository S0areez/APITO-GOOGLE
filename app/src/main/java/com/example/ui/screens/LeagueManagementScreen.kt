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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun LeagueManagementScreen(
    viewModel: ApitoViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var csvText by remember { mutableStateOf("") }
    var resultReport by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val sampleCsv = """
        2026-10-25, 14:00, Arena Morumbi - Campo 1, society
        2026-10-25, 15:30, Arena Morumbi - Campo 2, futebol
        2026-10-26, 19:00, Ginásio Pacaembu, futsal
        2026-10-27, 20:30, Clube Tietê - Quadra 4, society
    """.trimIndent()

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
                    text = "Gestão de Ligas & Torneios",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextPrimary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ApitoGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.EmojiEvents, null, tint = ApitoGold, modifier = Modifier.size(24.dp))
                        }

                        Column {
                            Text(
                                text = "Auto-Escalação de Árbitros",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary
                            )
                            Text(
                                text = "Cole a lista de jogos do campeonato. O algoritmo do Apito encontra os melhores árbitros compatíveis com a modalidade.",
                                fontSize = 11.sp,
                                color = ApitoTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tabela de Jogos (CSV):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextSecondary
                    )

                    OutlinedButton(
                        onClick = { csvText = sampleCsv },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.ContentPaste, null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preencher Exemplo", fontSize = 11.sp, color = ApitoGold)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    placeholder = {
                        Text(
                            text = "Data, Horário, Local, Modalidade\nExemplo:\n2026-10-25, 14:00, Arena Gol, society",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ApitoTextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("csv_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ApitoSurfaceElevated,
                        unfocusedContainerColor = ApitoSurfaceElevated,
                        focusedTextColor = ApitoTextPrimary,
                        unfocusedTextColor = ApitoTextPrimary,
                        focusedBorderColor = ApitoGold,
                        unfocusedBorderColor = ApitoBorder
                    )
                )
            }

            item {
                Button(
                    onClick = {
                        if (csvText.isBlank()) {
                            Toast.makeText(context, "Cole os dados dos jogos primeiro.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val report = viewModel.importLeagueMatches(csvText)
                        resultReport = report
                        Toast.makeText(context, "Jogos processados e árbitros escalados!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("process_league_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ApitoGold, contentColor = ApitoBackground),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Upload, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processar e Escalar Árbitros", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            if (resultReport != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ApitoGreen.copy(alpha = 0.15f))
                            .border(1.dp, ApitoGreen, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = ApitoGreen, modifier = Modifier.size(24.dp))
                            Column {
                                Text(
                                    text = "Escalação Concluída com Sucesso!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ApitoGreen
                                )
                                Text(
                                    text = "Foram criadas e associadas as partidas no seu Dashboard.",
                                    fontSize = 12.sp,
                                    color = ApitoTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
