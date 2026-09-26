package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.components.MatchItemCard
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary

@Composable
fun PartidasScreen(
    viewModel: ApitoViewModel,
    onNavigateToMatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val allMatches by viewModel.allMatches.collectAsState()
    val role = currentProfile?.role ?: "contratante"

    var selectedTab by remember { mutableStateOf("ativas") }

    val myMatches = allMatches.filter {
        if (role == "contratante") it.contractorId == currentProfile?.id || true else it.refereeId == currentProfile?.id || true
    }

    val filteredMatches = when (selectedTab) {
        "ativas" -> myMatches.filter { it.status in listOf("pendente", "aceita", "a_caminho", "em_andamento") }
        "finalizadas" -> myMatches.filter { it.status == "finalizada" }
        else -> myMatches.filter { it.status == "cancelada" }
    }.sortedByDescending { it.createdAt }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        Surface(color = ApitoSurface, tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Minhas Partidas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Pair("ativas", "Próximas & Ao Vivo"),
                        Pair("finalizadas", "Finalizadas"),
                        Pair("canceladas", "Canceladas")
                    ).forEach { (id, label) ->
                        val isSel = selectedTab == id
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedTab = id },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ApitoGold,
                                selectedLabelColor = ApitoBackground,
                                containerColor = ApitoSurfaceElevated,
                                labelColor = ApitoTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSel,
                                borderColor = if (isSel) ApitoGold else ApitoBorder
                            )
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredMatches.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚽", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhuma partida nesta categoria.",
                                fontSize = 14.sp,
                                color = ApitoTextMuted
                            )
                        }
                    }
                }
            } else {
                items(filteredMatches) { match ->
                    MatchItemCard(
                        match = match,
                        userRole = role,
                        onClick = { onNavigateToMatch(match.id) }
                    )
                }
            }
        }
    }
}
