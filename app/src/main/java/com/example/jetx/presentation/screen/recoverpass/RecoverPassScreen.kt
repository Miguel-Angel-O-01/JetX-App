package com.example.jetx.presentation.screen.recoverpass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetx.R
import com.example.jetx.presentation.components.JetXButton
import com.example.jetx.presentation.components.JetXButtonVariant
import com.example.jetx.presentation.components.JetXCapsuleField
import com.example.jetx.presentation.components.JetXGlassCard
import com.example.jetx.presentation.components.JetXGlassCardVariant
import com.example.jetx.presentation.components.JetXLoadingOverlay
import com.example.jetx.presentation.theme.ArchivoBlack
import com.example.jetx.presentation.theme.JetXTheme

@Composable
fun RecoverPassScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: RecoverPassViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RecoverPassScreenContent(
        uiState = uiState,
        onEmailChanged = viewModel::onEmailChanged,
        onSendRecoveryEmail = viewModel::sendRecoveryEmail,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun RecoverPassScreenContent(
    uiState: RecoverPassUiState,
    onEmailChanged: (String) -> Unit = {},
    onSendRecoveryEmail: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // 1. Imagen de fondo decorativa
        Image(
            painter = painterResource(id = R.drawable.loginimage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            contentScale = ContentScale.Crop
        )

        // 2. Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Jet X",
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = ArchivoBlack,
                modifier = Modifier.padding(top = 44.dp, bottom = 24.dp)
            )

            // Tarjeta central con estética de cristal azul noche
            JetXGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                variant = JetXGlassCardVariant.MIDNIGHT_BLUE,
                shape = RoundedCornerShape(32.dp),
                contentPadding = 24.dp
            ) {
                Text(
                    text = "Recuperar Contraseña",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (uiState.isSuccess) {
                        "Hemos enviado un enlace de recuperación a tu correo. Revisa tu bandeja de entrada o spam."
                    } else {
                        "Ingresa el correo registrado con tu cuenta para recibir las instrucciones de restablecimiento."
                    },
                    fontSize = 13.sp,
                    color = if (uiState.isSuccess) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (!uiState.isSuccess) {
                    JetXCapsuleField(
                        value = uiState.email,
                        onValueChange = onEmailChanged,
                        placeholder = "Correo electrónico",
                        keyboardType = KeyboardType.Email
                    )

                    uiState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    JetXButton(
                        text = "Enviar enlace",
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onSendRecoveryEmail()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        variant = JetXButtonVariant.PRIMARY,
                        isLoading = uiState.isLoading,
                        height = 48.dp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                JetXButton(
                    text = "Volver a Iniciar Sesión",
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.SECONDARY,
                    height = 48.dp
                )
            }
        }

        if (uiState.isLoading) {
            JetXLoadingOverlay(message = "Enviando solicitud...")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecoverPassScreenPreview() {
    JetXTheme {
        RecoverPassScreenContent(
            uiState = RecoverPassUiState()
        )
    }
}