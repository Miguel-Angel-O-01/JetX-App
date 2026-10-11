package com.example.jetx.presentation.screen.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user

                if (user != null) {
                    ensureUserDocumentExists(
                        uid = user.uid,
                        email = user.email ?: email,
                        displayName = user.displayName ?: ""
                    )
                }

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: FirebaseAuthInvalidUserException) {
                // Caso: El correo no está registrado en Firebase
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Esta cuenta no está registrada. Crea una cuenta para continuar."
                    )
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                // Caso: Contraseña incorrecta o credencial errónea
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Correo o contraseña incorrectos. Verifica tus datos."
                    )
                }
            } catch (e: FirebaseNetworkException) {
                // Caso: Sin conexión a internet
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
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user

                if (user != null) {
                    ensureUserDocumentExists(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName ?: ""
                    )
                }

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: FirebaseNetworkException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Sin conexión a internet al autenticar con Google"
                    )
                }
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

    private suspend fun ensureUserDocumentExists(uid: String, email: String, displayName: String) {
        try {
            val userDocRef = firestore.collection("users").document(uid)
            val snapshot = userDocRef.get().await()

            if (!snapshot.exists()) {
                val names = displayName.split(" ")
                val firstName = names.firstOrNull() ?: ""
                val lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""

                val userData = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email,
                    "role" to "CLIENT",
                    "createdAt" to FieldValue.serverTimestamp()
                )
                userDocRef.set(userData).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}