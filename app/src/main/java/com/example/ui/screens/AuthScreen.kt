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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@Composable
fun AuthScreen(
    viewModel: ApitoViewModel,
    onAuthSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Cadastro

    // Login Form State
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regPhone by remember { mutableStateOf("") }
    var regCity by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("contratante") } // "contratante" or "arbitro"

    val isLoading by viewModel.authLoading.collectAsState()
    val errorMessage by viewModel.authError.collectAsState()

    val scrollState = rememberScrollState()

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
            alpha = 0.25f
        )

        // Subtle gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ApitoBackground.copy(alpha = 0.7f),
                            ApitoBackground.copy(alpha = 0.92f),
                            ApitoBackground
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Branding Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ApitoGold.copy(alpha = 0.15f))
                    .border(2.dp, ApitoGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🏟️", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Apito",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = ApitoTextPrimary,
                letterSpacing = (-1).sp
            )

            Text(
                text = "Plataforma oficial para gestão e contratação de árbitros",
                fontSize = 12.sp,
                color = ApitoTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ApitoSurface.copy(alpha = 0.95f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ApitoBorder, ApitoBorder)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = ApitoCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = ApitoCyan,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            modifier = Modifier.testTag("tab_login"),
                            text = {
                                Text(
                                    text = "Entrar",
                                    fontSize = 15.sp,
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == 0) ApitoCyan else ApitoTextMuted
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            modifier = Modifier.testTag("tab_register"),
                            text = {
                                Text(
                                    text = "Criar Conta",
                                    fontSize = 15.sp,
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == 1) ApitoCyan else ApitoTextMuted
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Message Banner
                    if (!errorMessage.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ApitoRed.copy(alpha = 0.15f))
                                .border(1.dp, ApitoRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                color = ApitoRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // TAB 0: ENTRAR (LOGIN)
                    if (selectedTabIndex == 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            OutlinedTextField(
                                value = loginEmail,
                                onValueChange = { loginEmail = it },
                                label = { Text("E-mail") },
                                placeholder = { Text("seu@email.com") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = ApitoTextMuted)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("email_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Senha") },
                                placeholder = { Text("••••••••") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = ApitoTextMuted)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Alternar visibilidade",
                                            tint = ApitoTextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("password_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    viewModel.login(loginEmail, loginPassword) {
                                        onAuthSuccess(viewModel.selectedRole.value)
                                    }
                                },
                                enabled = !isLoading && loginEmail.isNotBlank() && loginPassword.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ApitoCyan,
                                    contentColor = ApitoBackground
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = ApitoBackground,
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Text(
                                        text = "Entrar no Apito",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    } else {
                        // TAB 1: CRIAR CONTA (REGISTER)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = regFullName,
                                onValueChange = { regFullName = it },
                                label = { Text("Nome Completo *") },
                                placeholder = { Text("Ex: João Silva") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = ApitoTextMuted)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("fullname_input"),
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

                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it },
                                label = { Text("E-mail *") },
                                placeholder = { Text("seu@email.com") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = ApitoTextMuted)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it },
                                label = { Text("Senha * (mínimo 6 caracteres)") },
                                placeholder = { Text("••••••••") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = ApitoTextMuted)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Alternar visibilidade",
                                            tint = ApitoTextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = regPhone,
                                    onValueChange = { regPhone = it },
                                    label = { Text("Telefone / Zap") },
                                    placeholder = { Text("(11) 99999-9999") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Phone, contentDescription = null, tint = ApitoTextMuted)
                                    },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                                OutlinedTextField(
                                    value = regCity,
                                    onValueChange = { regCity = it },
                                    label = { Text("Cidade - UF") },
                                    placeholder = { Text("São Paulo - SP") },
                                    leadingIcon = {
                                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ApitoTextMuted)
                                    },
                                    modifier = Modifier.weight(1f),
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
                            }

                            Text(
                                text = "Qual o seu perfil de uso?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ApitoTextPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            // Role Selection Cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Contratante
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (regRole == "contratante") ApitoGreen.copy(alpha = 0.2f) else ApitoSurfaceElevated)
                                        .border(
                                            width = if (regRole == "contratante") 2.dp else 1.dp,
                                            color = if (regRole == "contratante") ApitoGreen else ApitoBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { regRole = "contratante" }
                                        .padding(12.dp)
                                        .testTag("role_choice_contratante"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Filled.Assignment,
                                            contentDescription = null,
                                            tint = if (regRole == "contratante") ApitoGreen else ApitoTextMuted,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Contratante",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (regRole == "contratante") ApitoGreen else ApitoTextSecondary
                                        )
                                        Text(
                                            text = "Organizo partidas",
                                            fontSize = 10.sp,
                                            color = ApitoTextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Árbitro
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (regRole == "arbitro") ApitoCyan.copy(alpha = 0.2f) else ApitoSurfaceElevated)
                                        .border(
                                            width = if (regRole == "arbitro") 2.dp else 1.dp,
                                            color = if (regRole == "arbitro") ApitoCyan else ApitoBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { regRole = "arbitro" }
                                        .padding(12.dp)
                                        .testTag("role_choice_arbitro"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Filled.SportsSoccer,
                                            contentDescription = null,
                                            tint = if (regRole == "arbitro") ApitoCyan else ApitoTextMuted,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Árbitro",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (regRole == "arbitro") ApitoCyan else ApitoTextSecondary
                                        )
                                        Text(
                                            text = "Apito jogos",
                                            fontSize = 10.sp,
                                            color = ApitoTextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    viewModel.register(
                                        email = regEmail,
                                        pass = regPassword,
                                        fullName = regFullName,
                                        role = regRole,
                                        phone = regPhone,
                                        city = regCity
                                    ) {
                                        onAuthSuccess(regRole)
                                    }
                                },
                                enabled = !isLoading && regEmail.isNotBlank() && regPassword.length >= 6 && regFullName.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("register_submit_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (regRole == "contratante") ApitoGreen else ApitoCyan,
                                    contentColor = ApitoBackground
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = ApitoBackground,
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Text(
                                        text = "Criar Minha Conta",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
