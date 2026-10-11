package com.example.jetx.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun onFirstNameChange(v: String) = _uiState.update { it.copy(firstName = v, errorMessage = null) }
    fun onLastNameChange(v: String) = _uiState.update { it.copy(lastName = v, errorMessage = null) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, errorMessage = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, errorMessage = null) }
    fun onCountryCodeChange(v: String) = _uiState.update { it.copy(countryCode = v) }
    fun onPhoneNumberChange(v: String) = _uiState.update { it.copy(phoneNumber = v, errorMessage = null) }
    fun onCountryChange(v: String) = _uiState.update { it.copy(country = v, errorMessage = null) }
    fun onCityChange(v: String) = _uiState.update { it.copy(city = v, errorMessage = null) }
    fun onAddressChange(v: String) = _uiState.update { it.copy(address = v, errorMessage = null) }
    fun onPassportNumberChange(v: String) = _uiState.update { it.copy(passportNumber = v, errorMessage = null) }

    fun onPoliciesToggled(accepted: Boolean) {
        _uiState.update { it.copy(isPoliciesAccepted = accepted, errorMessage = null) }
    }

    fun onOpenPoliciesModal() {
        _uiState.update { it.copy(showPoliciesModal = true) }
    }

    fun onDismissPoliciesModal() {
        _uiState.update { it.copy(showPoliciesModal = false) }
    }

    fun onAcceptPoliciesFromModal() {
        _uiState.update { it.copy(isPoliciesAccepted = true, showPoliciesModal = false, errorMessage = null) }
    }

    fun registerUser(onSuccess: () -> Unit) {
        val s = _uiState.value
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.email.isBlank() ||
            s.password.isBlank() || s.phoneNumber.isBlank() || s.country.isBlank() ||
            s.city.isBlank() || s.address.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos requeridos") }
            return
        }

        if (!s.isPoliciesAccepted) {
            _uiState.update { it.copy(errorMessage = "Debes aceptar las políticas de privacidad para registrarte") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val cleanEmail = s.email.trim().lowercase()
                val cleanPassword = s.password.trim()

                // 1. Crear usuario en Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(cleanEmail, cleanPassword).await()
                val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el ID del usuario")

                // 2. Mapear datos a Firestore según la colección users
                val fullPhone = "(${s.countryCode}) ${s.phoneNumber.trim()}"
                val userData = hashMapOf(
                    "firstName" to s.firstName.trim(),
                    "lastName" to s.lastName.trim(),
                    "email" to cleanEmail,
                    "countryCode" to s.countryCode.trim(),
                    "phoneNumber" to s.phoneNumber.trim(),
                    "fullPhoneNumber" to fullPhone,
                    "country" to s.country.trim(),
                    "city" to s.city.trim(),
                    "address" to s.address.trim(),
                    "passportNumber" to s.passportNumber.trim(),
                    "role" to "CLIENT"
                )

                // Guardar en la colección users con el uid como ID del documento
                firestore.collection("users").document(uid).set(userData).await()

                // 3. Cerrar la sesión automática creada al registrarse
                auth.signOut()

                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error al registrar") }
            }
        }
    }
}
