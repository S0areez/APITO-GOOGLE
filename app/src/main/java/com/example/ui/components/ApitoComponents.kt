package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchEntity
import com.example.data.ProfileEntity
import com.example.ui.theme.ApitoBackground
import com.example.ui.theme.ApitoBlue
import com.example.ui.theme.ApitoBorder
import com.example.ui.theme.ApitoCyan
import com.example.ui.theme.ApitoGold
import com.example.ui.theme.ApitoGoldVariant
import com.example.ui.theme.ApitoGreen
import com.example.ui.theme.ApitoRed
import com.example.ui.theme.ApitoSurface
import com.example.ui.theme.ApitoSurfaceElevated
import com.example.ui.theme.ApitoSurfaceVariant
import com.example.ui.theme.ApitoTextMuted
import com.example.ui.theme.ApitoTextPrimary
import com.example.ui.theme.ApitoTextSecondary
import com.example.ui.theme.LevelBlack
import com.example.ui.theme.LevelBronze
import com.example.ui.theme.LevelGold
import com.example.ui.theme.LevelSilver
import java.util.Locale

@Composable
fun LevelBadge(level: String, modifier: Modifier = Modifier) {
    val (color, label, icon) = when (level.lowercase()) {
        "ouro" -> Triple(LevelGold, "Ouro", "🥇")
        "prata" -> Triple(LevelSilver, "Prata", "🥈")
        "black" -> Triple(LevelBlack, "Black", "🏆")
        else -> Triple(LevelBronze, "Bronze", "🥉")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 11.sp)
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun RatingStars(
    rating: Double,
    size: Dp = 14.dp,
    interactive: Boolean = false,
    onRatingChange: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..5) {
            val starModifier = if (interactive && onRatingChange != null) {
                Modifier
                    .size(size)
                    .clickable { onRatingChange(i.toFloat()) }
                    .padding(2.dp)
            } else {
                Modifier.size(size)
            }

            when {
                rating >= i -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Estrela $i",
                        tint = ApitoGold,
                        modifier = starModifier
                    )
                }
                rating >= i - 0.5 -> {
                    Icon(
                        imageVector = Icons.Filled.StarHalf,
                        contentDescription = "Meia Estrela",
                        tint = ApitoGold,
                        modifier = starModifier
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Outlined.StarOutline,
                        contentDescription = "Sem Estrela",
                        tint = ApitoTextMuted,
                        modifier = starModifier
                    )
                }
            }
        }
        if (!interactive) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format(Locale.US, "%.1f", rating),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ApitoTextSecondary
            )
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "pendente" -> Triple(ApitoGold.copy(alpha = 0.15f), ApitoGold, "Pendente")
        "aceita" -> Triple(ApitoBlue.copy(alpha = 0.15f), ApitoBlue, "Confirmada")
        "a_caminho" -> Triple(ApitoCyan.copy(alpha = 0.2f), ApitoCyan, "A Caminho")
        "em_andamento" -> Triple(ApitoGreen.copy(alpha = 0.2f), ApitoGreen, "● Ao Vivo")
        "finalizada" -> Triple(Color(0xFF334155), ApitoTextSecondary, "Finalizada")
        "cancelada" -> Triple(ApitoRed.copy(alpha = 0.15f), ApitoRed, "Cancelada")
        else -> Triple(ApitoSurfaceVariant, ApitoTextSecondary, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArbitroCard(
    referee: ProfileEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("referee_card_${referee.id}"),
        color = ApitoSurface
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials / badge
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ApitoGold.copy(alpha = 0.3f), ApitoCyan.copy(alpha = 0.3f))
                        )
                    )
                    .border(2.dp, ApitoGold.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = referee.fullName.take(2).uppercase(),
                    fontWeight = FontWeight.Black,
                    color = ApitoGold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = referee.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ApitoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (referee.isVerified) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verificado",
                            tint = ApitoCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = ApitoTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = referee.city,
                        fontSize = 12.sp,
                        color = ApitoTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    LevelBadge(level = referee.level)
                }

                Spacer(modifier = Modifier.height(4.dp))
                RatingStars(rating = referee.ratingAvg)

                // Modalities chips
                FlowRow(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    referee.getModalitiesList().take(3).forEach { mod ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ApitoSurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = mod.replace("_", " ").replaceFirstChar { it.uppercase() },
                                fontSize = 10.sp,
                                color = ApitoTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Price & arrow
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "R$ ${referee.hourlyRate.toInt()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = ApitoGold
                )
                Text(
                    text = "/jogo",
                    fontSize = 11.sp,
                    color = ApitoTextMuted
                )
            }
        }
    }
}

@Composable
fun MatchItemCard(
    match: MatchEntity,
    userRole: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ApitoBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("match_card_${match.id}"),
        color = ApitoSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SportsSoccer,
                        contentDescription = null,
                        tint = ApitoGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = match.modality.replace("_", " ").uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ApitoTextPrimary
                    )
                }
                StatusBadge(status = match.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = ApitoCyan,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${match.date} às ${match.time}",
                    fontSize = 12.sp,
                    color = ApitoTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = ApitoRed,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = match.location,
                    fontSize = 12.sp,
                    color = ApitoTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (userRole == "contratante") "Árbitro: ${match.refereeName}" else "Contratante: ${match.contractorName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ApitoTextPrimary
                )
                Text(
                    text = "R$ ${match.price.toInt()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = ApitoGold
                )
            }
        }
    }
}

@Composable
fun ApitoHeader(
    title: String,
    subtitle: String? = null,
    currentRole: String,
    onRoleSwitch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ApitoBackground
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = ApitoTextPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = ApitoTextSecondary
                    )
                }
            }

            // Role Switcher Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ApitoSurfaceElevated)
                    .border(1.dp, ApitoGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onRoleSwitch)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("role_switcher_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (currentRole == "contratante") "📋 Contratante" else "⚽ Árbitro",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApitoGold
                )
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = "Mudar Papel",
                    tint = ApitoGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ApitoBottomNav(
    currentRoute: String,
    role: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = ApitoSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav")
    ) {
        val items = if (role == "contratante") {
            listOf(
                Triple("inicio", "Início", Icons.Filled.Dashboard),
                Triple("busca", "Buscar", Icons.Filled.Search),
                Triple("partidas", "Partidas", Icons.Filled.SportsSoccer),
                Triple("carteira", "Carteira", Icons.Filled.Wallet),
                Triple("perfil", "Perfil", Icons.Filled.Person)
            )
        } else {
            listOf(
                Triple("inicio", "Início", Icons.Filled.Dashboard),
                Triple("solicitacoes", "Solicitações", Icons.Filled.Notifications),
                Triple("partidas", "Jogos", Icons.Filled.SportsSoccer),
                Triple("carteira", "Carteira", Icons.Filled.Wallet),
                Triple("perfil", "Perfil", Icons.Filled.Person)
            )
        }

        items.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ApitoBackground,
                    selectedTextColor = ApitoGold,
                    indicatorColor = ApitoGold,
                    unselectedIconColor = ApitoTextMuted,
                    unselectedTextColor = ApitoTextMuted
                ),
                modifier = Modifier.testTag("nav_item_$route")
            )
        }
    }
}
