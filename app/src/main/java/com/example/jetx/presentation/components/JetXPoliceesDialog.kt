package com.example.jetx.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun JetXPoliciesDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E20))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Políticas de Privacidad",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = """
                            Bienvenido a Jet X. La protección de su privacidad y sus datos personales es fundamental para nosotros.
                            
                            1. Información recopilada:
                            Recopilamos información necesaria para prestar nuestros servicios de alquiler y reserva de jets privados, como nombres, credenciales de contacto y preferencias de vuelo.
                            
                            2. Uso de la información:
                            Sus datos se emplean estrictamente para la gestión de reservas, validación de identidad y emisión de confirmaciones operativas de vuelo.
                            
                            3. Seguridad:
                            Implementamos medidas de cifrado y estándares de seguridad avanzados para salvaguardar su información en todo momento.
                            
                            4. Derechos del usuario:
                            Usted podrá solicitar la actualización, supresión o consulta de sus datos registrados comunicándose con nuestro equipo de soporte.
                            
                            Al continuar utilizando la aplicación, usted declara haber leído y comprendido los términos aquí expuestos.
                        """.trimIndent(),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                JetXButton(
                    text = "Aceptar y continuar",
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth(),
                    variant = JetXButtonVariant.PRIMARY
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun JetXPoliciesDialogPreview() {
    JetXPoliciesDialog(
        onDismiss = {},
        onAccept = {}
    )
}