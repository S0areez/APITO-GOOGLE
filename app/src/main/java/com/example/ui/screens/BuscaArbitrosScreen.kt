package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.components.ArbitroCard
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoSurfaceVariant
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaArbitrosScreen(
    viewModel: ApitoViewModel,
    onNavigateToReferee: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedModality by viewModel.selectedModality.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val maxPrice by viewModel.maxPrice.collectAsState()
    val referees by viewModel.filteredReferees.collectAsState()

    val focusManager = LocalFocusManager.current
    val isSyncing by viewModel.isSyncingSupabase.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showFilterPanel by remember { mutableStateOf(false) }

    val modalities = listOf(
        Pair("Todos", null),
        Pair("Society", "society"),
        Pair("Futebol Campo", "futebol"),
        Pair("Futsal", "futsal"),
        Pair("Futebol 7", "futebol_7")
    )

    val equipmentOptions = listOf("Apito", "Cartões", "Cronômetro", "Súmula", "Placar")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Search Header Bar
        Surface(
            color = ApitoSurface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Buscar Árbitros",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = { Text("Nome, cidade ou arena...", fontSize = 13.sp, color = ApitoTextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = ApitoGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Limpar", tint = ApitoTextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ApitoSurfaceElevated,
                            unfocusedContainerColor = ApitoSurfaceElevated,
                            focusedBorderColor = ApitoGold,
                            unfocusedBorderColor = ApitoBorder,
                            focusedTextColor = ApitoTextPrimary,
                            unfocusedTextColor = ApitoTextPrimary
                        )
                    )

                    // Toggle filter button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (showFilterPanel) ApitoGold else ApitoSurfaceElevated)
                            .border(1.dp, if (showFilterPanel) ApitoGold else ApitoBorder, RoundedCornerShape(14.dp))
                            .clickable { showFilterPanel = !showFilterPanel }
                            .testTag("toggle_filters_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filtros",
                            tint = if (showFilterPanel) ApitoBackground else ApitoGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Horizontal Modality Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modalities.forEach { (label, value) ->
                        val isSelected = selectedModality == value
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectedModality.value = value },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ApitoGold,
                                selectedLabelColor = ApitoBackground,
                                containerColor = ApitoSurfaceElevated,
                                labelColor = ApitoTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) ApitoGold else ApitoBorder
                            )
                        )
                    }
                }

                // Collapsible Filter Panel for Equipment & Max Price
                if (showFilterPanel) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ApitoSurfaceVariant)
                            .padding(14.dp)
                    ) {
                        // Max Price Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Preço máximo por jogo:", fontSize = 12.sp, color = ApitoTextSecondary)
                            Text(text = "R$ ${maxPrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ApitoGold)
                        }

                        Slider(
                            value = maxPrice,
                            onValueChange = { viewModel.maxPrice.value = it },
                            valueRange = 80f..300f,
                            steps = 10,
                            colors = SliderDefaults.colors(
                                thumbColor = ApitoGold,
                                activeTrackColor = ApitoGold,
                                inactiveTrackColor = ApitoBorder
                            ),
                            modifier = Modifier.testTag("price_filter_slider")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Equipamentos obrigatórios:", fontSize = 12.sp, color = ApitoTextSecondary)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            equipmentOptions.forEach { eq ->
                                val hasEq = selectedEquipment.contains(eq)
                                FilterChip(
                                    selected = hasEq,
                                    onClick = {
                                        val updated = selectedEquipment.toMutableSet()
                                        if (hasEq) updated.remove(eq) else updated.add(eq)
                                        viewModel.selectedEquipment.value = updated
                                    },
                                    label = { Text(eq, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ApitoCyan,
                                        selectedLabelColor = ApitoBackground,
                                        containerColor = ApitoSurface,
                                        labelColor = ApitoTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Results Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${referees.size} árbitros disponíveis",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ApitoTextMuted
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (referees.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔍", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Nenhum árbitro encontrado",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary
                            )
                            Text(
                                text = "Tente relaxar os filtros de modalidade ou preço.",
                                fontSize = 12.sp,
                                color = ApitoTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(referees) { referee ->
                    ArbitroCard(
                        referee = referee,
                        onClick = { onNavigateToReferee(referee.id) }
                    )
                }
            }
        }
    }
}
