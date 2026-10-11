package com.example.jetx.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetx.presentation.screen.booking.BookingScreen
import com.example.jetx.presentation.theme.JetXTheme

enum class BottomNavTab(val label: String, val icon: ImageVector) {
    INICIO("Inicio", Icons.Default.Flight),
    SERVICIOS("Opciones", Icons.Default.RoomService),
    ACTIVIDAD("Historial", Icons.Default.CardTravel),
    CUENTA("Cuenta", Icons.Default.Person)
}

@Composable
fun JetXFloatingBottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // Cápsula contenedora principal
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF141923).copy(alpha = 0.92f))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.values().forEach { tab ->
                val isSelected = tab == selectedTab

                val animatedBgColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White.copy(alpha = 0.16f) else Color.Transparent,
                    animationSpec = tween(durationMillis = 250),
                    label = "tab_pill_bg"
                )

                val animatedContentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.50f),
                    animationSpec = tween(durationMillis = 200),
                    label = "tab_pill_content"
                )

                // Mini-cápsula activa
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(animatedBgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = animatedContentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.label,
                                color = animatedContentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
fun JetXFloatingBottomBarPreview() {
    var selectedTab by remember { mutableStateOf(BottomNavTab.INICIO) }
    JetXTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            JetXFloatingBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}
