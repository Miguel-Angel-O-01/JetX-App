package com.example.jetx.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun JetXMap(
    originLat: Double,
    originLon: Double,
    destLat: Double,
    destLon: Double,
    currentLat: Double = (originLat + destLat) / 2,
    currentLon: Double = (originLon + destLon) / 2,
    currentBearing: Double = 0.0,
    currentAltitudeMetres: Double = 0.0,
    outsideTempCelsius: Double = 15.0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (!LocalInspectionMode.current) {
            WorldWindMapInternal(
                originLat = originLat,
                originLon = originLon,
                destLat = destLat,
                destLon = destLon,
                currentLat = currentLat,
                currentLon = currentLon,
                currentBearing = currentBearing,
                currentAltitudeMetres = currentAltitudeMetres,
                outsideTempCelsius = outsideTempCelsius
            )
        } else {
            Text("WorldWind Map Preview", color = Color.White)
        }
    }
}