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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wallet
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.ApitoRed
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CarteiraScreen(
    viewModel: ApitoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val transactions by viewModel.transactions.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmount by remember { mutableStateOf("100") }
    var pixKey by remember { mutableStateOf(currentProfile?.phone ?: "11987654321") }

    val totalEntradas = transactions.filter { it.type == "entrada" }.sumOf { it.amount }
    val totalSaidas = transactions.filter { it.type in listOf("saque", "saida") }.sumOf { it.amount }
    val saldo = (totalEntradas - totalSaidas).coerceAtLeast(0.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Top Header
        Surface(color = ApitoSurface, tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Carteira & Repasses",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary
                )
                Text(
                    text = "Acompanhe seus rendimentos e repasses de arbitragem.",
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
            // Main Balance Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("wallet_balance_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1E3A8A), Color(0xFF0284C7))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Saldo Disponível",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Filled.Wallet,
                                    contentDescription = null,
                                    tint = ApitoGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "R$ ${saldo.toInt()}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showWithdrawDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ApitoCyan,
                                    contentColor = ApitoBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("withdraw_pix_button")
                            ) {
                                Icon(Icons.Filled.QrCode, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sacar via PIX",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Summary Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ArrowDownward, null, tint = ApitoGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Entradas", fontSize = 12.sp, color = ApitoTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "R$ ${totalEntradas.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoGreen
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ArrowUpward, null, tint = ApitoRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Saques / Pagos", fontSize = 12.sp, color = ApitoTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "R$ ${totalSaidas.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoRed
                            )
                        }
                    }
                }
            }

            // Transaction History Header
            item {
                Text(
                    text = "Histórico de Transações",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoTextPrimary
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma movimentação registrada.",
                        fontSize = 12.sp,
                        color = ApitoTextMuted
                    )
                }
            } else {
                items(transactions) { tx ->
                    val isEntrada = tx.type == "entrada"
                    val (icon, color, sign) = if (isEntrada) {
                        Triple(Icons.Filled.ArrowDownward, ApitoGreen, "+")
                    } else {
                        Triple(Icons.Filled.ArrowUpward, ApitoRed, "-")
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ApitoBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.description,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                Text(
                                    text = dateFormat.format(Date(tx.createdAt)),
                                    fontSize = 11.sp,
                                    color = ApitoTextMuted
                                )
                            }

                            Text(
                                text = "$sign R$ ${tx.amount.toInt()}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to request withdrawal via PIX
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = ApitoSurface,
            title = {
                Text("Sacar via PIX", color = ApitoTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Saldo disponível: R$ ${saldo.toInt()}",
                        fontSize = 13.sp,
                        color = ApitoGold,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "⚡ Repasse instantâneo via Abacate Pay PIX",
                        fontSize = 11.sp,
                        color = ApitoGreen,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("Valor do Saque (R$)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ApitoTextPrimary,
                            unfocusedTextColor = ApitoTextPrimary,
                            focusedBorderColor = ApitoGold,
                            unfocusedBorderColor = ApitoBorder
                        )
                    )

                    OutlinedTextField(
                        value = pixKey,
                        onValueChange = { pixKey = it },
                        label = { Text("Chave PIX (CPF/Email/Telefone)") },
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
                        val amount = withdrawAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && amount <= saldo) {
                            viewModel.withdrawFunds(amount, pixKey) {
                                showWithdrawDialog = false
                                Toast.makeText(context, "Saque de R$ ${amount.toInt()} solicitado com sucesso!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Valor inválido ou maior que o saldo.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ApitoGold, contentColor = ApitoBackground)
                ) {
                    Text("Confirmar Saque")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancelar", color = ApitoTextMuted)
                }
            }
        )
    }
}
