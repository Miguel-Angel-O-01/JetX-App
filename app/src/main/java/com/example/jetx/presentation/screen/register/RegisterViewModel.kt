package com.example.jetx.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

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
                kotlinx.coroutines.delay(1000)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error al registrar") }
            }
        }
    }
}
