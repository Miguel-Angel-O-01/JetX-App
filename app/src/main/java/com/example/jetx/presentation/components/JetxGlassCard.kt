package com.example.jetx.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class JetXGlassCardVariant {
    SMOKE_DARK,    // Gris neutro ahumado (Ideal para HomeScreen con video)
    MIDNIGHT_BLUE  // Azul noche translúcido (Ideal para LoginScreen y RegisterScreen)
}

@Composable
fun JetXGlassCard(
    modifier: Modifier = Modifier,
    variant: JetXGlassCardVariant = JetXGlassCardVariant.SMOKE_DARK,
    shape: Shape = RoundedCornerShape(28.dp),
    contentPadding: Dp = 22.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val backgroundColor = when (variant) {
        JetXGlassCardVariant.SMOKE_DARK -> Color(0xFF2C2C2E).copy(alpha = 0.65f)
        JetXGlassCardVariant.MIDNIGHT_BLUE -> Color(0xFF070B14).copy(alpha = 0.75f)
    }

    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
fun JetXGlassCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        JetXGlassCard(variant = JetXGlassCardVariant.SMOKE_DARK) {
            androidx.compose.material3.Text("Smoke Dark Variant", color = Color.White)
        }
        JetXGlassCard(variant = JetXGlassCardVariant.MIDNIGHT_BLUE) {
            androidx.compose.material3.Text("Midnight Blue Variant", color = Color.White)
        }
    }
}