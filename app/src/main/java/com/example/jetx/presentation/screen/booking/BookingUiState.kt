package com.example.jetx.presentation.screen.booking

data class Airport(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val icaoCode: String,
    val lat: Double,
    val lon: Double
)

data class FlightRoute(
    val id: String,
    val origin: Airport,
    val dest: Airport,
    val progress: Double,
    val isEmptyLeg: Boolean = false
)

data class BookingUiState(
    // Datos de reserva
    val originIcao: String = "KMIA (Miami)",
    val destinationIcao: String = "MMTO (Toluca)",
    val aircraftModel: String = "Gulfstream G600",
    val estimatedPriceUsd: Double = 14500.0,

    // Coordenadas para el globo terráqueo
    val originLat: Double = 25.7959,
    val originLon: Double = -80.2870,
    val destLat: Double = 19.3371,
    val destLon: Double = -99.5660,

    // Posición animada en vivo
    val currentLat: Double = 25.7959,
    val currentLon: Double = -80.2870,
    val flightProgress: Double = 0.0, // 0.0 a 1.0

    // Telemetría del avión animado
    val currentBearing: Double = 0.0,
    val currentAltitudeMetres: Double = 0.0,
    val outsideTempCelsius: Double = 15.0,
    val flightMode: String = "CRUISE",
    val isLoading: Boolean = false,
    
    // UI Selector & Interacción Multivuelo
    val isSelectorExpanded: Boolean = false,
    val availableAirports: List<Airport> = emptyList(),
    val backgroundFlights: List<FlightRoute> = emptyList(),
    val selectedBackgroundFlightId: String? = null, // ID del vuelo que tocamos en el mapa
    val selectedOrigin: Airport = Airport("1", "Miami International", "Miami", "USA", "KMIA", 25.7959, -80.2870),
    val selectedDestination: Airport = Airport("2", "Toluca International", "Toluca", "Mexico", "MMTO", 19.3371, -99.5660)
)