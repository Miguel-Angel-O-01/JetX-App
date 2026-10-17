package com.example.jetx

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidationUnitTest {

    // Función simple de validación local
    private fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.endsWith(".com")
    }

    private fun isPasswordSecure(password: String): Boolean {
        return password.length >= 6
    }

    @Test
    fun login_email_validation_works_correctly() {
        // Correo correcto
        assertTrue(isEmailValid("pasajero@jetx.com"))
        // Correo inválido sin arroba
        assertFalse(isEmailValid("pasajerojetx.com"))
    }

    @Test
    fun register_password_length_is_enforced() {
        // Contraseña corta inválida
        assertFalse(isPasswordSecure("123"))
        // Contraseña válida
        assertTrue(isPasswordSecure("ClaveSegura2026"))
    }
}