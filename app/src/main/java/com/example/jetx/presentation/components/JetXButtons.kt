package com.example.jetx.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class JetXButtonVariant {
    PRIMARY,
    SECONDARY,
    GOOGLE
}

@Composable
fun JetXButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: JetXButtonVariant = JetXButtonVariant.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    height: Dp = 48.dp
) {
    val containerColor = when (variant) {
        JetXButtonVariant.PRIMARY -> Color.White
        JetXButtonVariant.SECONDARY -> Color.White.copy(alpha = 0.15f)
        JetXButtonVariant.GOOGLE -> Color(0xFF1E293B)
    }

    val contentColor = when (variant) {
        JetXButtonVariant.PRIMARY -> Color.Black
        JetXButtonVariant.SECONDARY -> Color.White
        JetXButtonVariant.GOOGLE -> Color.White
    }

    val border = when (variant) {
        JetXButtonVariant.SECONDARY -> BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
        JetXButtonVariant.GOOGLE -> BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
        else -> null
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
fun JetXButtonsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        JetXButton(text = "Primary Button", onClick = {})
        JetXButton(text = "Secondary Button", variant = JetXButtonVariant.SECONDARY, onClick = {})
        JetXButton(text = "Google Button", variant = JetXButtonVariant.GOOGLE, onClick = {})
        JetXButton(text = "Loading Button", isLoading = true, onClick = {})
    }
}