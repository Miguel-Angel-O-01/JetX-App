package com.example.jetx.presentation.screen.home

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.jetx.R
import com.example.jetx.presentation.components.JetXButton
import com.example.jetx.presentation.components.JetXButtonVariant
import com.example.jetx.presentation.components.JetXGlassCard
import com.example.jetx.presentation.theme.ArchivoBlack
import com.example.jetx.presentation.theme.JetXTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. FONDO: Video en bucle con overlay oscuro
        BackgroundVideoPlayer(
            videoResId = R.raw.homelading
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.50f))
        )

        // 2. CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Título superior
            Text(
                text = "Jet X",
                fontFamily = ArchivoBlack,
                fontSize = 36.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Tarjeta translúcida central con los accesos principales
            JetXGlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 22.dp
            ) {
                // Botón 1: Iniciar sesión
                JetXButton(
                    text = "Iniciar sesión",
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.SECONDARY,
                    height = 50.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botón 2: Crear cuenta
                JetXButton(
                    text = "Crear cuenta",
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.SECONDARY,
                    height = 50.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botón 3: Iniciar como invitado (lleva a Booking)
                JetXButton(
                    text = "Iniciar como invitado",
                    onClick = onContinueAsGuest,
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.SECONDARY,
                    height = 50.dp
                )
            }

            // Pie de página legal
            Text(
                text = "© 2026 Jet X. Todos los derechos reservados.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun BackgroundVideoPlayer(
    videoResId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isInPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    
    if (isInPreview) {
        // Placeholder for Android Studio Preview
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Background Video", color = Color.White)
        }
        return
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val rawUri = Uri.parse("android.resource://${context.packageName}/$videoResId")
            setMediaItem(MediaItem.fromUri(rawUri))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    JetXTheme {
        HomeScreen()
    }
}