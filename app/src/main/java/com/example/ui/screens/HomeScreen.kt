package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBlue
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary

@Composable
fun HomeScreen(
    onSelectRole: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Hero Stadium Background
        Image(
            painter = painterResource(id = R.drawable.img_stadium_hero),
            contentDescription = "Estádio Apito",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

        // Dark gradient overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ApitoBackground.copy(alpha = 0.5f),
                            ApitoBackground.copy(alpha = 0.85f),
                            ApitoBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Branding Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Gold Whistle Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ApitoGold.copy(alpha = 0.15f))
                        .border(2.dp, ApitoGold.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏟️", fontSize = 40.sp)
                }

                Text(
                    text = "Apito",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary,
                    letterSpacing = (-1).sp
                )

                Text(
                    text = "Eleve o nível do seu jogo.\nContrate árbitros profissionais ou gerencie sua carreira.",
                    fontSize = 14.sp,
                    color = ApitoTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Community pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ApitoSurfaceElevated.copy(alpha = 0.8f))
                        .border(1.dp, ApitoGold.copy(alpha = 0.25f), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "⚽", fontSize = 12.sp)
                    Text(
                        text = "+500 árbitros • 120 arenas cadastradas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ApitoGold
                    )
                }
            }

            // Role selection buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "ESCOLHA SEU PERFIL PARA ENTRAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 1.sp
                )

                // Contractor Choice Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, ApitoGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onSelectRole("contratante") }
                        .testTag("role_button_contratante"),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ApitoGreen.copy(alpha = 0.2f))
                                .border(1.dp, ApitoGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assignment,
                                contentDescription = null,
                                tint = ApitoGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Sou Contratante",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary
                            )
                            Text(
                                text = "Organizo jogos e procuro juiz qualificado para meu time ou campeonato.",
                                fontSize = 12.sp,
                                color = ApitoTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Referee Choice Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, ApitoCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onSelectRole("arbitro") }
                        .testTag("role_button_arbitro"),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ApitoCyan.copy(alpha = 0.2f))
                                .border(1.dp, ApitoCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SportsSoccer,
                                contentDescription = null,
                                tint = ApitoCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Sou Árbitro",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary
                            )
                            Text(
                                text = "Quero apitar partidas amadoras, gerenciar agenda e receber com segurança.",
                                fontSize = 12.sp,
                                color = ApitoTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
