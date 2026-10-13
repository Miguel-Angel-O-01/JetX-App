package com.example.jetx.presentation.screen.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(input: String) {
        _uiState.update { it.copy(username = input, errorMessage = null) }
    }

    fun onPasswordChanged(input: String) {
        _uiState.update { it.copy(password = input, errorMessage = null) }
    }

    fun onGoogleSignInError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    fun login(onSuccess: () -> Unit) {
        val email = _uiState.value.username.trim().lowercase()
        val password = _uiState.value.password.trim()

        // 1. Validación de campos vacíos
        if (email.isBlank() && password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos") }
            return
        }

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa tu correo electrónico") }
            return
        }

        // 2. Validación de formato de correo
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "El formato del correo electrónico no es válido") }
            return
        }

        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa tu contraseña") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Ocurrió un error inesperado al iniciar sesión"
                    )
                }
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: e.message ?: "Error al autenticar con Google"
                    )
                }
            }
        }
    }
}