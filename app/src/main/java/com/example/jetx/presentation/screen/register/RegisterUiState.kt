package com.example.jetx.presentation.screen.register

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val country: String = "",
    val countryCode: String = "+503",
    val phoneNumber: String = "",
    val city: String = "",
    val address: String = "",
    val passportNumber: String = "",
    val isPoliciesAccepted: Boolean = false,
    val showPoliciesModal: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)