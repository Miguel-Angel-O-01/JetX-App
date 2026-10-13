package com.example.jetx.presentation.screen.recoverpass

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecoverPassViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecoverPassUiState())
    val uiState: StateFlow<RecoverPassUiState> = _uiState.asStateFlow()

    fun onEmailChanged(input: String) {
        _uiState.update { it.copy(email = input, errorMessage = null) }
    }

    fun sendRecoveryEmail() {
        val cleanEmail = _uiState.value.email.trim()

        if (cleanEmail.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa tu correo electrónico") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(errorMessage = "El formato de correo no es válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Error al enviar el correo de recuperación"
                    )
                }
            }
        }
    }
}