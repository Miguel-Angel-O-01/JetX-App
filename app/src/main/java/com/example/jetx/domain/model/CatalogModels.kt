package com.example.jetx.domain.model

data class DestinationItem(
    val id: String,
    val city: String,
    val country: String,
    val icaoCode: String,
    val estimatedFlightHours: String,
    val recommendedJet: String,
    val imageResId: Int? = null // Deja null o añade tu R.drawable después
)

data class EventItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val location: String,
    val tag: String, // Ej: "FÓRMULA 1", "VIP EXCLUSIVE"
    val imageResId: Int? = null
)

data class ServiceOptionItem(
    val id: String,
    val title: String,
    val description: String,
    val tag: String // Ej: "EMPTY LEGS", "CHAUFFEUR"
)