package com.example.jetx.presentation.screen.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

class BookingViewModel : ViewModel() {

    private val defaultAirports = listOf(
        Airport("1", "Miami International", "Miami", "USA", "KMIA", 25.7959, -80.2870),
        Airport("2", "Toluca International", "Toluca", "Mexico", "MMTO", 19.3371, -99.5660),
        Airport("3", "Los Angeles International", "Los Angeles", "USA", "KLAX", 33.9416, -118.4085),
        Airport("4", "JFK International", "New York", "USA", "KJFK", 40.6413, -73.7781),
        Airport("5", "Heathrow", "London", "UK", "EGLL", 51.4700, -0.4543),
        Airport("6", "Charles de Gaulle", "Paris", "France", "LFPG", 49.0097, 2.5479),
        Airport("7", "Dubai International", "Dubai", "UAE", "OMDB", 25.2532, 55.3657)
    )

    private val bgFlights = listOf(
        FlightRoute("bg1", defaultAirports[2], defaultAirports[3], 0.35, isEmptyLeg = true),  // LAX -> JFK (Golden Empty Leg)
        FlightRoute("bg2", defaultAirports[4], defaultAirports[0], 0.60, isEmptyLeg = false), // LHR -> MIA (Ghost full flight)
        FlightRoute("bg3", defaultAirports[6], defaultAirports[5], 0.45, isEmptyLeg = true)   // DXB -> CDG (Golden Empty Leg)
    )

    private val _uiState = MutableStateFlow(BookingUiState(availableAirports = defaultAirports, backgroundFlights = bgFlights))
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private var flightJob: Job? = null

    init {
        startTelemetrySimulation()
    }

    private fun startTelemetrySimulation(totalSteps: Int = 600) {
        flightJob?.cancel()
        flightJob = viewModelScope.launch {
            var step = 0
            while (true) {
                delay(60)
                val s = _uiState.value
                val progress = step.toDouble() / totalSteps
                
                // 1. Interpolación de posición geodésica actual
                val (currLat, currLon) = interpolateGeodesic(s.originLat, s.originLon, s.destLat, s.destLon, progress)
                
                // 2. Interpolación del siguiente punto para calcular rumbo (bearing)
                val nextProgress = ((step + 1).toDouble() / totalSteps).coerceAtMost(1.0)
                val (nextLat, nextLon) = interpolateGeodesic(s.originLat, s.originLon, s.destLat, s.destLon, nextProgress)
                val bearing = calculateBearing(currLat, currLon, nextLat, nextLon)

                // 3. Altitud y temperatura parabólica
                val maxAltitude = 12000.0
                val altitude = 4 * maxAltitude * progress * (1 - progress)
                val temp = (15.0 - (6.5 * (altitude / 1000.0))).coerceIn(-56.5, 15.0)

                // 4. Modo de vuelo
                val mode = when {
                    progress < 0.20 -> "CLIMB"
                    progress > 0.80 -> "DESCENT"
                    else -> "CRUISE"
                }

                _uiState.update {
                    it.copy(
                        currentLat = currLat,
                        currentLon = currLon,
                        flightProgress = progress,
                        currentAltitudeMetres = altitude,
                        outsideTempCelsius = temp,
                        currentBearing = bearing,
                        flightMode = mode
                    )
                }
                step = if (step < totalSteps) step + 1 else 0
            }
        }
    }

    fun toggleSelectorExpanded() {
        _uiState.update { it.copy(isSelectorExpanded = !it.isSelectorExpanded) }
    }

    fun selectDestination(airport: Airport) {
        _uiState.update { 
            it.copy(
                selectedDestination = airport,
                destLat = airport.lat,
                destLon = airport.lon,
                destinationIcao = airport.icaoCode,
                isSelectorExpanded = false,
                selectedBackgroundFlightId = null // Quitamos el HUD si seleccionamos un vuelo nuevo
            ) 
        }
        startTelemetrySimulation()
    }

    fun onFlightTapped(flightId: String) {
        _uiState.update { it.copy(selectedBackgroundFlightId = flightId) }
    }

    fun dismissFlightHud() {
        _uiState.update { it.copy(selectedBackgroundFlightId = null) }
    }
}

private fun interpolateGeodesic(lat1Deg: Double, lon1Deg: Double, lat2Deg: Double, lon2Deg: Double, fraction: Double): Pair<Double, Double> {
    val lat1 = Math.toRadians(lat1Deg)
    val lon1 = Math.toRadians(lon1Deg)
    val lat2 = Math.toRadians(lat2Deg)
    val lon2 = Math.toRadians(lon2Deg)

    val dLat = lat2 - lat1
    val dLon = lon2 - lon1

    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val d = 2 * atan2(sqrt(a), sqrt(1 - a))

    if (d == 0.0) return Pair(lat1Deg, lon1Deg)

    val A = sin((1 - fraction) * d) / sin(d)
    val B = sin(fraction * d) / sin(d)

    val x = A * cos(lat1) * cos(lon1) + B * cos(lat2) * cos(lon2)
    val y = A * cos(lat1) * sin(lon1) + B * cos(lat2) * sin(lon2)
    val z = A * sin(lat1) + B * sin(lat2)

    val latRad = atan2(z, sqrt(x * x + y * y))
    val lonRad = atan2(y, x)

    return Pair(Math.toDegrees(latRad), Math.toDegrees(lonRad))
}

private fun calculateBearing(lat1Deg: Double, lon1Deg: Double, lat2Deg: Double, lon2Deg: Double): Double {
    val lat1 = Math.toRadians(lat1Deg)
    val lat2 = Math.toRadians(lat2Deg)
    val dLon = Math.toRadians(lon2Deg - lon1Deg)

    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    val brng = Math.toDegrees(atan2(y, x))
    return (brng + 360.0) % 360.0
}