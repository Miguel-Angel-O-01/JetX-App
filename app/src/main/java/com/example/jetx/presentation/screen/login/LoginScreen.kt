package com.example.jetx.presentation.screen.login

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.service.autofill.OnClickAction
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetx.R
import com.example.jetx.presentation.components.JetXButton
import com.example.jetx.presentation.components.JetXButtonVariant
import com.example.jetx.presentation.components.JetXCapsuleField
import com.example.jetx.presentation.components.JetXGlassCard
import com.example.jetx.presentation.components.JetXGlassCardVariant
import com.example.jetx.presentation.components.JetXLoadingOverlay
import com.example.jetx.presentation.theme.ArchivoBlack
import com.example.jetx.presentation.theme.JetXTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import com.example.jetx.presentation.screen.recoverpass.RecoverPassScreen

fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToRecoverPass: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LoginScreenContent(
        uiState = uiState,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClick = { viewModel.login(onLoginSuccess) },
        onGoogleSignInClick = { idToken -> viewModel.signInWithGoogle(idToken, onLoginSuccess) },
        onGoogleSignInError = viewModel::onGoogleSignInError,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToRecoverPass = onNavigateToRecoverPass
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onGoogleSignInClick: (String) -> Unit = {},
    onGoogleSignInError: (String) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToRecoverPass: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.login_title),
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = ArchivoBlack,
                modifier = Modifier.padding(top = 44.dp, bottom = 24.dp)
            )

            JetXGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                variant = JetXGlassCardVariant.MIDNIGHT_BLUE,
                shape = RoundedCornerShape(32.dp),
                contentPadding = 22.dp
            ) {
                JetXCapsuleField(
                    value = uiState.username,
                    onValueChange = onUsernameChanged,
                    placeholder = stringResource(id = R.string.hint_username),
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                JetXCapsuleField(
                    value = uiState.password,
                    onValueChange = onPasswordChanged,
                    placeholder = stringResource(id = R.string.hint_password),
                    isPassword = true,
                    keyboardType = KeyboardType.Password
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable { onNavigateToRecoverPass() }
                )

                uiState.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = error,
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Botón Iniciar Sesión convencional
                JetXButton(
                    text = stringResource(id = R.string.btn_login),
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onLoginClick()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.PRIMARY,
                    isLoading = uiState.isLoading,
                    height = 48.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Dentro de LoginScreenContent en el botón de Google:
                JetXButton(
                    text = "Continuar con Google",
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        coroutineScope.launch {
                            try {
                                val activity = context.findActivity() ?: (context as? Activity)
                                val targetContext = activity ?: context
                                val credentialManager = CredentialManager.create(targetContext)

                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setAutoSelectEnabled(false)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = targetContext
                                )

                                val credential = result.credential
                                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    onGoogleSignInClick(googleIdTokenCredential.idToken)
                                }
                            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                                // El usuario simplemente cerró la ventana de Google
                            } catch (e: Exception) {
                                e.printStackTrace()
                                val msg = e.localizedMessage ?: e.message ?: "Error al conectar con Google"
                                onGoogleSignInError("Error con Google: $msg")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.GOOGLE,
                    isLoading = uiState.isLoading,
                    height = 48.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.new_client),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }

        if (uiState.isLoading) {
            JetXLoadingOverlay(message = "Autenticando...")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    JetXTheme {
        LoginScreenContent(
            uiState = LoginUiState()
        )
    }
}