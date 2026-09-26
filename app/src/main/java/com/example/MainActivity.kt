package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ApitoViewModel
import com.example.ui.components.ApitoBottomNav
import com.example.ui.screens.ArbitroDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BuscaArbitrosScreen
import com.example.ui.screens.CarteiraScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.ContratanteDashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeagueManagementScreen
import com.example.ui.screens.ModoPartidaScreen
import com.example.ui.screens.PartidasAbertasScreen
import com.example.ui.screens.PartidasScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.PerfilArbitroScreen
import com.example.ui.screens.PerfilUsuarioScreen
import com.example.ui.screens.SolicitacoesScreen
import com.example.ui.theme.MyApplicationTheme

sealed class Screen {
    object Auth : Screen()
    object Home : Screen()
    object ContratanteDashboard : Screen()
    object BuscaArbitros : Screen()
    data class PerfilArbitro(val refereeId: String) : Screen()
    data class Checkout(val refereeId: String) : Screen()
    data class Payment(val matchId: String) : Screen()
    data class ModoPartida(val matchId: String) : Screen()
    object ArbitroDashboard : Screen()
    object PartidasAbertas : Screen()
    object Solicitacoes : Screen()
    object Partidas : Screen()
    object Carteira : Screen()
    object LeagueManagement : Screen()
    object PerfilUsuario : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ApitoApp()
            }
        }
    }
}

@Composable
fun ApitoApp(viewModel: ApitoViewModel = viewModel()) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()

    val initialScreen = remember {
        if (viewModel.isAuthenticated.value) {
            if (viewModel.selectedRole.value == "contratante") Screen.ContratanteDashboard else Screen.ArbitroDashboard
        } else {
            Screen.Auth
        }
    }
    val screenStack = remember { mutableStateListOf<Screen>(initialScreen) }
    val currentScreen = screenStack.lastOrNull() ?: initialScreen

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
    }

    fun navigateBack() {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
        }
    }

    fun replaceWith(screen: Screen) {
        screenStack.clear()
        screenStack.add(screen)
    }

    BackHandler(enabled = screenStack.size > 1) {
        navigateBack()
    }

    // Determine current bottom nav route
    val currentBottomRoute = when (currentScreen) {
        is Screen.ContratanteDashboard, is Screen.ArbitroDashboard -> "inicio"
        is Screen.BuscaArbitros -> "busca"
        is Screen.Solicitacoes -> "solicitacoes"
        is Screen.Partidas -> "partidas"
        is Screen.Carteira -> "carteira"
        is Screen.PerfilUsuario -> "perfil"
        else -> null
    }

    val showBottomNav = currentBottomRoute != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                ApitoBottomNav(
                    currentRoute = currentBottomRoute ?: "inicio",
                    role = selectedRole,
                    onNavigate = { route ->
                        when (route) {
                            "inicio" -> {
                                val dest = if (selectedRole == "contratante") Screen.ContratanteDashboard else Screen.ArbitroDashboard
                                replaceWith(dest)
                            }
                            "busca" -> replaceWith(Screen.BuscaArbitros)
                            "solicitacoes" -> replaceWith(Screen.Solicitacoes)
                            "partidas" -> replaceWith(Screen.Partidas)
                            "carteira" -> replaceWith(Screen.Carteira)
                            "perfil" -> replaceWith(Screen.PerfilUsuario)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Auth -> {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { role ->
                            val dest = if (role == "contratante") Screen.ContratanteDashboard else Screen.ArbitroDashboard
                            replaceWith(dest)
                        }
                    )
                }

                is Screen.Home -> {
                    HomeScreen(
                        onSelectRole = { role ->
                            viewModel.switchRole(role)
                            if (role == "contratante") {
                                replaceWith(Screen.ContratanteDashboard)
                            } else {
                                replaceWith(Screen.ArbitroDashboard)
                            }
                        }
                    )
                }

                is Screen.ContratanteDashboard -> {
                    ContratanteDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = { navigateTo(Screen.BuscaArbitros) },
                        onNavigateToReferee = { refId -> navigateTo(Screen.PerfilArbitro(refId)) },
                        onNavigateToMatch = { matchId -> navigateTo(Screen.ModoPartida(matchId)) },
                        onNavigateToLeague = { navigateTo(Screen.LeagueManagement) },
                        onRoleSwitch = {
                            viewModel.switchRole("arbitro")
                            replaceWith(Screen.ArbitroDashboard)
                        }
                    )
                }

                is Screen.BuscaArbitros -> {
                    BuscaArbitrosScreen(
                        viewModel = viewModel,
                        onNavigateToReferee = { refId -> navigateTo(Screen.PerfilArbitro(refId)) }
                    )
                }

                is Screen.PerfilArbitro -> {
                    PerfilArbitroScreen(
                        refereeId = screen.refereeId,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onNavigateToCheckout = { refId -> navigateTo(Screen.Checkout(refId)) }
                    )
                }

                is Screen.Checkout -> {
                    CheckoutScreen(
                        refereeId = screen.refereeId,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onProceedToPayment = { matchId -> navigateTo(Screen.Payment(matchId)) }
                    )
                }

                is Screen.Payment -> {
                    PaymentScreen(
                        matchId = screen.matchId,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onPaymentSuccess = { matchId ->
                            navigateTo(Screen.ModoPartida(matchId))
                        }
                    )
                }

                is Screen.ModoPartida -> {
                    ModoPartidaScreen(
                        matchId = screen.matchId,
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.ArbitroDashboard -> {
                    ArbitroDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToSolicitacoes = { navigateTo(Screen.Solicitacoes) },
                        onNavigateToMatch = { matchId -> navigateTo(Screen.ModoPartida(matchId)) },
                        onNavigateToWallet = { replaceWith(Screen.Carteira) },
                        onNavigateToOpenMatches = { navigateTo(Screen.PartidasAbertas) },
                        onRoleSwitch = {
                            viewModel.switchRole("contratante")
                            replaceWith(Screen.ContratanteDashboard)
                        }
                    )
                }

                is Screen.PartidasAbertas -> {
                    PartidasAbertasScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onNavigateToMatch = { matchId -> navigateTo(Screen.ModoPartida(matchId)) }
                    )
                }

                is Screen.Solicitacoes -> {
                    SolicitacoesScreen(
                        viewModel = viewModel,
                        onNavigateToMatch = { matchId -> navigateTo(Screen.ModoPartida(matchId)) }
                    )
                }

                is Screen.Partidas -> {
                    PartidasScreen(
                        viewModel = viewModel,
                        onNavigateToMatch = { matchId -> navigateTo(Screen.ModoPartida(matchId)) }
                    )
                }

                is Screen.Carteira -> {
                    CarteiraScreen(
                        viewModel = viewModel
                    )
                }

                is Screen.LeagueManagement -> {
                    LeagueManagementScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }

                is Screen.PerfilUsuario -> {
                    PerfilUsuarioScreen(
                        viewModel = viewModel,
                        onLogout = {
                            viewModel.logout {
                                replaceWith(Screen.Auth)
                            }
                        },
                        onRoleSwitch = {
                            val newRole = if (selectedRole == "contratante") "arbitro" else "contratante"
                            viewModel.switchRole(newRole)
                            if (newRole == "contratante") {
                                replaceWith(Screen.ContratanteDashboard)
                            } else {
                                replaceWith(Screen.ArbitroDashboard)
                            }
                        }
                    )
                }
            }
        }
    }
}
