package com.example.jetx.presentation.screen.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetx.R
import com.example.jetx.presentation.components.CountryDropdownField
import com.example.jetx.presentation.components.CountryInfo
import com.example.jetx.presentation.components.JetXButton
import com.example.jetx.presentation.components.JetXButtonVariant
import com.example.jetx.presentation.components.JetXCapsuleField
import com.example.jetx.presentation.components.JetXGlassCard
import com.example.jetx.presentation.components.JetXGlassCardVariant
import com.example.jetx.presentation.components.JetXPoliciesDialog
import com.example.jetx.presentation.theme.ArchivoBlack
import com.example.jetx.presentation.theme.JetXTheme

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    RegisterScreenContent(
        uiState = uiState,
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onCountryCodeChange = viewModel::onCountryCodeChange,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onCountryChange = viewModel::onCountryChange,
        onCityChange = viewModel::onCityChange,
        onAddressChange = viewModel::onAddressChange,
        onPassportNumberChange = viewModel::onPassportNumberChange,
        onPoliciesToggled = viewModel::onPoliciesToggled,
        onOpenPoliciesModal = viewModel::onOpenPoliciesModal,
        onDismissPoliciesModal = viewModel::onDismissPoliciesModal,
        onAcceptPoliciesFromModal = viewModel::onAcceptPoliciesFromModal,
        onRegisterClick = { viewModel.registerUser(onSuccess = onRegisterSuccess) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onCountryCodeChange: (String) -> Unit = {},
    onPhoneNumberChange: (String) -> Unit = {},
    onCountryChange: (String) -> Unit = {},
    onCityChange: (String) -> Unit = {},
    onAddressChange: (String) -> Unit = {},
    onPassportNumberChange: (String) -> Unit = {},
    onPoliciesToggled: (Boolean) -> Unit = {},
    onOpenPoliciesModal: () -> Unit = {},
    onDismissPoliciesModal: () -> Unit = {},
    onAcceptPoliciesFromModal: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val cardScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // Fondo decorativo inferior
        Image(
            painter = painterResource(id = R.drawable.loginimage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
            contentScale = ContentScale.Crop
        )

        // Overlay oscuro para contraste
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // ESTRUCTURA PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. CABECERA FIJA
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.register_header),
                fontFamily = ArchivoBlack,
                fontSize = 30.sp,
                color = Color.White
            )

            Text(
                text = stringResource(R.string.register_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. CAJA DE REGISTRO CON JetXGlassCard (MIDNIGHT_BLUE)
            JetXGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                variant = JetXGlassCardVariant.MIDNIGHT_BLUE, // Azul noche translúcido idéntico al Login
                shape = RoundedCornerShape(28.dp),
                contentPadding = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .verticalScroll(cardScrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    JetXCapsuleField(
                        value = uiState.firstName,
                        onValueChange = onFirstNameChange,
                        placeholder = stringResource(R.string.register_first_name)
                    )

                    JetXCapsuleField(
                        value = uiState.lastName,
                        onValueChange = onLastNameChange,
                        placeholder = stringResource(R.string.register_last_name)
                    )

                    JetXCapsuleField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.register_email),
                        keyboardType = KeyboardType.Email
                    )

                    JetXCapsuleField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.register_password),
                        isPassword = true,
                        keyboardType = KeyboardType.Password
                    )

                    // Fila País y Ciudad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CountryDropdownField(
                            selectedCountry = uiState.country,
                            onCountrySelected = { countryInfo ->
                                onCountryChange("${countryInfo.flag} ${countryInfo.name}")
                                onCountryCodeChange(countryInfo.code)
                            },
                            placeholder = stringResource(R.string.register_country),
                            modifier = Modifier.weight(1f)
                        )
                        JetXCapsuleField(
                            value = uiState.city,
                            onValueChange = onCityChange,
                            placeholder = stringResource(R.string.register_city),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fila Código (+503) y Teléfono
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        JetXCapsuleField(
                            value = uiState.countryCode,
                            onValueChange = {},
                            placeholder = "+503",
                            readOnly = true,
                            modifier = Modifier.weight(0.28f),
                            keyboardType = KeyboardType.Phone
                        )
                        JetXCapsuleField(
                            value = uiState.phoneNumber,
                            onValueChange = onPhoneNumberChange,
                            placeholder = stringResource(R.string.register_phone_number),
                            modifier = Modifier.weight(0.72f),
                            keyboardType = KeyboardType.Phone
                        )
                    }

                    JetXCapsuleField(
                        value = uiState.address,
                        onValueChange = onAddressChange,
                        placeholder = stringResource(R.string.register_address)
                    )

                    JetXCapsuleField(
                        value = uiState.passportNumber,
                        onValueChange = onPassportNumberChange,
                        placeholder = stringResource(R.string.register_passport)
                    )

                    // Checkbox y enlace de Políticas de Privacidad
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.isPoliciesAccepted,
                            onCheckedChange = onPoliciesToggled,
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.White,
                                uncheckedColor = Color.White.copy(alpha = 0.6f),
                                checkmarkColor = Color.Black
                            )
                        )
                        Text(
                            text = "Acepto las políticas de privacidad",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onOpenPoliciesModal() }
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Botones inferiores usando JetXButton
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JetXButton(
                            text = stringResource(R.string.register_btn_cancel),
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(0.42f),
                            variant = JetXButtonVariant.SECONDARY,
                            height = 48.dp
                        )

                        JetXButton(
                            text = stringResource(R.string.register_btn_submit),
                            onClick = onRegisterClick,
                            modifier = Modifier.weight(0.58f),
                            variant = JetXButtonVariant.PRIMARY,
                            isLoading = uiState.isLoading,
                            height = 48.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Diálogo de Políticas centralizado
        if (uiState.showPoliciesModal) {
            JetXPoliciesDialog(
                onDismiss = onDismissPoliciesModal,
                onAccept = onAcceptPoliciesFromModal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    JetXTheme {
        RegisterScreenContent(
            uiState = RegisterUiState()
        )
    }
}