package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ApitoViewModel
import com.example.ui.components.LevelBadge
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

@Composable
fun PerfilUsuarioScreen(
    viewModel: ApitoViewModel,
    onLogout: () -> Unit,
    onRoleSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentProfile by viewModel.currentProfile.collectAsState()
    val isReferee = currentProfile?.role == "arbitro"

    var name by remember(currentProfile) { mutableStateOf(currentProfile?.fullName ?: "") }
    var phone by remember(currentProfile) { mutableStateOf(currentProfile?.phone ?: "") }
    var city by remember(currentProfile) { mutableStateOf(currentProfile?.city ?: "") }
    var bio by remember(currentProfile) { mutableStateOf(currentProfile?.bio ?: "") }
    var rate by remember(currentProfile) { mutableStateOf((currentProfile?.hourlyRate?.toInt() ?: 120).toString()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ApitoBackground)
    ) {
        // Header
        Surface(color = ApitoSurface, tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Meu Perfil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary
                )
                Text(
                    text = "Gerencie seus dados e preferências da conta Apito.",
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
            // User Avatar Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ApitoGold.copy(alpha = 0.2f))
                                .border(2.dp, ApitoGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (currentProfile?.fullName ?: "AP").take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = ApitoGold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = currentProfile?.fullName ?: "Usuário",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ApitoTextPrimary
                                )
                                if (currentProfile?.isVerified == true) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = "Verificado",
                                        tint = ApitoCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = currentProfile?.email ?: "usuario@apito.com",
                                fontSize = 12.sp,
                                color = ApitoTextMuted
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isReferee) ApitoCyan.copy(alpha = 0.2f) else ApitoGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isReferee) "Árbitro Oficial" else "Contratante",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isReferee) ApitoCyan else ApitoGreen
                                    )
                                }

                                if (isReferee && currentProfile?.level != null) {
                                    LevelBadge(level = currentProfile?.level ?: "prata")
                                }
                            }
                        }
                    }
                }
            }

            // Quick Role Switcher
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, ApitoGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = ApitoSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Alternar Visão de Papel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary
                            )
                            Text(
                                text = if (isReferee) "Mudar para perfil de Contratante" else "Mudar para perfil de Árbitro",
                                fontSize = 11.sp,
                                color = ApitoTextSecondary
                            )
                        }

                        Button(
                            onClick = onRoleSwitch,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ApitoGold,
                                contentColor = ApitoBackground
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("switch_role_action_button")
                        ) {
                            Icon(Icons.Filled.SwapHoriz, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Alternar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Edit Profile Form
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Informações Cadastrais",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ApitoTextPrimary
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome Completo") },
                            leadingIcon = { Icon(Icons.Filled.Person, null, tint = ApitoGold) },
                            modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ApitoSurfaceElevated,
                                unfocusedContainerColor = ApitoSurfaceElevated,
                                focusedTextColor = ApitoTextPrimary,
                                unfocusedTextColor = ApitoTextPrimary,
                                focusedBorderColor = ApitoGold,
                                unfocusedBorderColor = ApitoBorder
                            )
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefone / WhatsApp") },
                            leadingIcon = { Icon(Icons.Filled.Phone, null, tint = ApitoCyan) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ApitoSurfaceElevated,
                                unfocusedContainerColor = ApitoSurfaceElevated,
                                focusedTextColor = ApitoTextPrimary,
                                unfocusedTextColor = ApitoTextPrimary,
                                focusedBorderColor = ApitoGold,
                                unfocusedBorderColor = ApitoBorder
                            )
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Cidade / Região") },
                            leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = ApitoRed) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ApitoSurfaceElevated,
                                unfocusedContainerColor = ApitoSurfaceElevated,
                                focusedTextColor = ApitoTextPrimary,
                                unfocusedTextColor = ApitoTextPrimary,
                                focusedBorderColor = ApitoGold,
                                unfocusedBorderColor = ApitoBorder
                            )
                        )

                        if (isReferee) {
                            OutlinedTextField(
                                value = rate,
                                onValueChange = { rate = it },
                                label = { Text("Valor cobrado por jogo (R$)") },
                                modifier = Modifier.fillMaxWidth(),
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

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Biografia / Experiência") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ApitoSurfaceElevated,
                                unfocusedContainerColor = ApitoSurfaceElevated,
                                focusedTextColor = ApitoTextPrimary,
                                unfocusedTextColor = ApitoTextPrimary,
                                focusedBorderColor = ApitoGold,
                                unfocusedBorderColor = ApitoBorder
                            )
                        )

                        Button(
                            onClick = {
                                val current = currentProfile ?: return@Button
                                val updated = current.copy(
                                    fullName = name,
                                    phone = phone,
                                    city = city,
                                    bio = bio,
                                    hourlyRate = rate.toDoubleOrNull() ?: current.hourlyRate
                                )
                                viewModel.updateProfile(updated)
                                Toast.makeText(context, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("save_profile_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ApitoGold, contentColor = ApitoBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Save, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salvar Alterações", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Logout / Return to Login
            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("logout_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = ApitoRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair da Conta", color = ApitoRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
