package com.example.jetx.presentation.screen.recoverpass

data class RecoverPassUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)