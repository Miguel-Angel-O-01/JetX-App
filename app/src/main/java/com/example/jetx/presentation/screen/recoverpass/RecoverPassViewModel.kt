package com.example.jetx.presentation.screen.recoverpass

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecoverPassViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                // Función oficial de Firebase para enviar el correo de recuperación
                auth.sendPasswordResetEmail(cleanEmail).await()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No existe una cuenta registrada con este correo"
                    )
                }
            } catch (e: FirebaseNetworkException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Sin conexión a internet. Verifica tu red."
                    )
                }
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