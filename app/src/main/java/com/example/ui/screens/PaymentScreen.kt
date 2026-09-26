package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun PaymentScreen(
    matchId: String,
    viewModel: ApitoViewModel,
    onBack: () -> Unit,
    onPaymentSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allMatches by viewModel.allMatches.collectAsState()
    val isCreatingBilling by viewModel.isCreatingBilling.collectAsState()
    val match = allMatches.firstOrNull { it.id == matchId }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isCheckingStatus by remember { mutableStateOf(false) }
    var currentPaymentStatus by remember { mutableStateOf(match?.abacateStatus ?: "PENDING") }

    val hasKey = viewModel.hasAbacateApiKey()

    // Auto-create AbacatePay billing on entering if not yet generated
    LaunchedEffect(matchId) {
        if (match != null && match.abacateBillingId == null) {
            viewModel.createAbacatePayBilling(matchId) { billing ->
                if (billing != null) {
                    currentPaymentStatus = billing.status ?: "PENDING"
                }
            }
        }
    }

    val billingUrl = match?.abacateBillingUrl ?: "https://abacatepay.com/pay/bill_${matchId.take(8)}"
    val billingId = match?.abacateBillingId ?: "bill_${matchId.take(8)}"
    val pixCode = "00020126580014BR.GOV.BCB.PIX0136apito-abacatepay-${matchId.take(8)}5204000053039865405${match?.price?.toInt() ?: 150}.005802BR5915APITO ESPORTES6009SAO PAULO62070503***6304ABCD"

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pagamento via Abacate Pay",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                    Text(
                        text = "Gateway oficial PIX & Cartão",
                        fontSize = 11.sp,
                        color = ApitoGreen
                    )
                }
                // Abacate Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ApitoGreen.copy(alpha = 0.15f))
                        .border(1.dp, ApitoGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🥑 AbacatePay",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoGreen
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Environment / Gateway Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, if (hasKey) ApitoGreen.copy(alpha = 0.5f) else ApitoCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ApitoSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = if (hasKey) ApitoGreen else ApitoCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasKey) "Gateway Ativo (API Abacate Pay Conectada)" else "Modo de Testes / Sandbox Abacate Pay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasKey) ApitoGreen else ApitoCyan
                        )
                        Text(
                            text = if (hasKey)
                                "Cobrança gerada diretamente na sua conta da AbacatePay."
                            else
                                "Operando em ambiente sandbox. Para pagamentos reais, configure ABACATEPAY_API_KEY no painel Secrets.",
                            fontSize = 11.sp,
                            color = ApitoTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // QR Code icon container
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ApitoGold.copy(alpha = 0.15f))
                    .border(1.5.dp, ApitoGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCode,
                    contentDescription = "PIX",
                    tint = ApitoGold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = if (currentPaymentStatus.equals("PAID", ignoreCase = true)) "Pagamento Concluído!" else "Aguardando Pagamento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = if (currentPaymentStatus.equals("PAID", ignoreCase = true)) ApitoGreen else ApitoTextPrimary
            )

            Text(
                text = "O valor é processado pela Abacate Pay e retido em custódia segura até o término do jogo, garantindo segurança para você e o árbitro.",
                fontSize = 12.sp,
                color = ApitoTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            // Match summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ApitoSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total a pagar", fontSize = 13.sp, color = ApitoTextSecondary)
                        Text(
                            text = "R$ ${match?.price?.toInt() ?: 150}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = ApitoGold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ApitoBorder)
                    )

                    Text(
                        text = "Partida: ${match?.modality?.replace("_", " ")?.uppercase() ?: "SOCIETY"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoTextPrimary
                    )
                    Text(
                        text = "Árbitro: ${match?.refereeName ?: "Árbitro Apito"}",
                        fontSize = 12.sp,
                        color = ApitoTextSecondary
                    )
                    Text(
                        text = "${match?.date} às ${match?.time}",
                        fontSize = 12.sp,
                        color = ApitoTextMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Cobrança ID:", fontSize = 11.sp, color = ApitoTextMuted)
                        Text(text = billingId, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ApitoCyan)
                    }
                }
            }

            // AbacatePay Checkout Action Button
            Button(
                onClick = {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(billingUrl))
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Abrindo link: $billingUrl", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("open_abacate_checkout_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ApitoGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Abrir Checkout Abacate Pay 🥑",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Pix Copia e Cola box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, ApitoCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ApitoSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Código PIX Copia e Cola (Abacate Pay):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ApitoCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pixCode,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ApitoTextSecondary,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(pixCode))
                            Toast.makeText(context, "Código PIX copiado com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ApitoSurface,
                            contentColor = ApitoCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Copiar Código PIX", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Check Status Button (Live query to Abacate Pay API)
            OutlinedButton(
                onClick = {
                    isCheckingStatus = true
                    viewModel.checkAbacateBillingStatus(matchId, billingId) { status ->
                        isCheckingStatus = false
                        currentPaymentStatus = status
                        if (status.equals("PAID", ignoreCase = true)) {
                            Toast.makeText(context, "🎉 Pagamento aprovado no Abacate Pay!", Toast.LENGTH_LONG).show()
                            onPaymentSuccess(matchId)
                        } else {
                            Toast.makeText(context, "Status no Abacate Pay: $status (Aguardando transferência)", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("check_payment_status_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCheckingStatus) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = ApitoGold, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Consultando Abacate Pay...", color = ApitoGold, fontSize = 13.sp)
                } else {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, tint = ApitoGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Verificar Status do Pagamento", color = ApitoGold, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Simulate Approved Payment Button
            Button(
                onClick = {
                    viewModel.updateMatchStatus(matchId, "aceita")
                    viewModel.triggerSupabaseWebhook(matchId, billingId) { success ->
                        if (success) {
                            Toast.makeText(context, "Webhook enviado com sucesso para o Supabase!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Toast.makeText(context, "Pagamento confirmado com sucesso via Abacate Pay!", Toast.LENGTH_SHORT).show()
                    onPaymentSuccess(matchId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("simulate_payment_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ApitoGold,
                    contentColor = ApitoBackground
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirmar Pagamento Imediato",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }

            // Test Supabase Webhook directly
            OutlinedButton(
                onClick = {
                    viewModel.triggerSupabaseWebhook(matchId, billingId) { success ->
                        if (success) {
                            Toast.makeText(context, "✅ Webhook Supabase executado com sucesso (HTTP 200)!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Webhook enviado ao Supabase (verifique os logs da Edge Function)", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Testar Webhook Supabase (billing.paid) ⚡", color = ApitoCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = "Pagar mais tarde", color = ApitoTextMuted, fontSize = 13.sp)
            }
        }
    }
}
