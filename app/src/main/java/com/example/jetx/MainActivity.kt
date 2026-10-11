package com.example.jetx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.jetx.presentation.screen.booking.BookingScreen
import com.example.jetx.presentation.screen.home.HomeScreen
import com.example.jetx.presentation.screen.login.LoginScreen
import com.example.jetx.presentation.screen.recoverpass.RecoverPassScreen
import com.example.jetx.presentation.screen.register.RegisterScreen
import com.example.jetx.presentation.theme.JetXTheme

enum class ScreenState {
    HOME,
    LOGIN,
    RECOVER_PASS,
    REGISTER,
    BOOKING
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetXTheme {
                var currentScreen by remember { mutableStateOf(ScreenState.HOME) }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        ScreenState.HOME -> {
                            HomeScreen(
                                onNavigateToLogin = { currentScreen = ScreenState.LOGIN },
                                onNavigateToRegister = { currentScreen = ScreenState.REGISTER },
                                onContinueAsGuest = { currentScreen = ScreenState.BOOKING }
                            )
                        }

                        ScreenState.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = ScreenState.BOOKING },
                                onNavigateToRegister = { currentScreen = ScreenState.REGISTER },
                                onNavigateToRecoverPass = { currentScreen = ScreenState.RECOVER_PASS }
                            )
                        }
                        ScreenState.RECOVER_PASS -> {
                            RecoverPassScreen(
                                onNavigateBack = { currentScreen = ScreenState.LOGIN }
                            )
                        }

                        ScreenState.REGISTER -> {
                            RegisterScreen(
                                onNavigateBack = { currentScreen = ScreenState.LOGIN },
                                onRegisterSuccess = { currentScreen = ScreenState.BOOKING }
                            )
                        }

                        ScreenState.BOOKING -> {
                            BookingScreen(
                                onNavigateBack = { currentScreen = ScreenState.HOME }
                            )
                        }
                    }
                }
            }
        }
    }
}