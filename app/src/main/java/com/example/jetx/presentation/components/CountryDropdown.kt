package com.example.jetx.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CountryInfo(
    val name: String,
    val flag: String,
    val code: String
)

val countriesList = listOf(
    CountryInfo("El Salvador", "🇸🇻", "+503"),
    CountryInfo("Guatemala", "🇬🇹", "+502"),
    CountryInfo("Honduras", "🇭🇳", "+504"),
    CountryInfo("Nicaragua", "🇳🇮", "+505"),
    CountryInfo("Costa Rica", "🇨🇷", "+506"),
    CountryInfo("Panamá", "🇵🇦", "+507"),
    CountryInfo("México", "🇲🇽", "+52"),
    CountryInfo("Estados Unidos", "🇺🇸", "+1"),
    CountryInfo("Canadá", "🇨🇦", "+1"),
    CountryInfo("España", "🇪🇸", "+34"),
    CountryInfo("Colombia", "🇨🇴", "+57"),
    CountryInfo("Argentina", "🇦🇷", "+54"),
    CountryInfo("Chile", "🇨🇱", "+56"),
    CountryInfo("Perú", "🇵🇪", "+51"),
    CountryInfo("Ecuador", "🇪🇨", "+593"),
    CountryInfo("República Dominicana", "🇩🇴", "+1"),
    CountryInfo("Uruguay", "🇺🇾", "+598"),
    CountryInfo("Paraguay", "🇵🇾", "+595"),
    CountryInfo("Bolivia", "🇧🇴", "+591"),
    CountryInfo("Brasil", "🇧🇷", "+55"),
    CountryInfo("Reino Unido", "🇬🇧", "+44"),
    CountryInfo("Alemania", "🇩🇪", "+49"),
    CountryInfo("Francia", "🇫🇷", "+33"),
    CountryInfo("Italia", "🇮🇹", "+39")
)

@Composable
fun CountryDropdownField(
    selectedCountry: String,
    onCountrySelected: (CountryInfo) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        JetXCapsuleField(
            value = selectedCountry,
            onValueChange = {},
            placeholder = placeholder,
            readOnly = true,
            onClick = { expanded = !expanded },
            trailingIcon = {
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Seleccionar país",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 140.dp)
                .background(Color(0xFF0F172A))
        ) {
            countriesList.forEach { country ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${country.flag} ${country.name}",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onCountrySelected(country)
                        expanded = false
                    }
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
fun CountryDropdownFieldPreview() {
    var selectedCountry by remember { mutableStateOf("") }
    Box(modifier = Modifier.padding(16.dp)) {
        CountryDropdownField(
            selectedCountry = selectedCountry,
            onCountrySelected = { selectedCountry = "${it.flag} ${it.name}" },
            placeholder = "Selecciona un país"
        )
    }
}
