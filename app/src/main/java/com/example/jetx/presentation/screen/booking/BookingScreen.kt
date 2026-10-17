package com.example.jetx.presentation.screen.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetx.domain.model.DestinationItem
import com.example.jetx.domain.model.EventItem
import com.example.jetx.domain.model.ServiceOptionItem
import com.example.jetx.presentation.components.*
import com.example.jetx.presentation.theme.ArchivoBlack

@Composable
fun BookingScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: BookingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentTab by remember { mutableStateOf(BottomNavTab.INICIO) }

    // Proporción generosa del mapa (65% del alto de la pantalla para máxima fluidez táctil)
    val configuration = LocalConfiguration.current
    val globeHeight = configuration.screenHeightDp.dp * 0.65f

    val destinations = remember {
        listOf(
            DestinationItem("1", "Londres", "Reino Unido", "EGLF", "8h 15m", "Gulfstream G600"),
            DestinationItem("2", "Nueva York", "Estados Unidos", "KTEB", "2h 45m", "Phenom 300E"),
            DestinationItem("3", "Singapur", "Singapur", "WSSL", "16h 30m", "Global 7500"),
            DestinationItem("4", "Dubái", "EAU", "OMDB", "13h 10m", "Citation Longitude")
        )
    }

    val vipEvents = remember {
        listOf(
            EventItem("e1", "Gran Premio F1 Mónaco", "Aterrizaje en Niza + Transfer en helicóptero", "Mónaco", "FÓRMULA 1"),
            EventItem("e2", "Art Basel Miami", "Acceso exclusivo FBO Opa-Locka VIP Lounge", "Miami, FL", "ARTE & CULTURA"),
            EventItem("e3", "World Economic Forum", "Vuelo privado directo a Zúrich + Escolta", "Davos, Suiza", "NEGOCIOS")
        )
    }

    val vipServices = remember {
        listOf(
            ServiceOptionItem("s1", "Empty Legs VIP", "Ahorra hasta 65% en vuelos de reposicionamiento vacíos.", "TARIFA SMART"),
            ServiceOptionItem("s2", "Traslado Puerta a Pista", "Mercedes Maybach privado directo a la rampa del FBO.", "CHAUFFEUR"),
            ServiceOptionItem("s3", "Catering Michelin", "Degustación de caviar y alta gastronomía a bordo.", "GOURMET")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ==========================================
            // SECCIÓN SUPERIOR: GLOBO 3D + BÚSQUEDA INTEGRADA
            // ==========================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(globeHeight)
                ) {
                    // 1. LIENZO DEL GLOBO (Fondo interactivo completo)
                    JetXMap(
                        originLat = uiState.originLat,
                        originLon = uiState.originLon,
                        destLat = uiState.destLat,
                        destLon = uiState.destLon,
                        currentLat = uiState.currentLat,
                        currentLon = uiState.currentLon,
                        currentBearing = uiState.currentBearing,
                        currentAltitudeMetres = uiState.currentAltitudeMetres,
                        outsideTempCelsius = uiState.outsideTempCelsius,
                        backgroundFlights = uiState.backgroundFlights,
                        onFlightTapped = { flightId -> viewModel.onFlightTapped(flightId) },
                        modifier = Modifier.fillMaxSize()
                    )

                    // 2. CAPA SUPERIOR: Encabezado y Cápsula de Búsqueda Flotante
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Título y Perfil
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Jet X",
                                fontFamily = ArchivoBlack,
                                fontSize = 32.sp,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .align(Alignment.CenterEnd)
                                    .clickable { onNavigateBack() }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Cápsula "¿A dónde vas?" integrada sobre el globo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF1E2430).copy(alpha = 0.80f))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                                .clickable { viewModel.toggleSelectorExpanded() }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FlightTakeoff,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (uiState.isSelectorExpanded) {
                                            "${uiState.selectedOrigin.icaoCode} ➔ ${uiState.selectedDestination.icaoCode}"
                                        } else {
                                            "¿A dónde vas?"
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = if (uiState.isSelectorExpanded) "Cerrar" else "Buscar",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        // Selector desplegable hacia abajo (sin recortar el globo)
                        AnimatedVisibility(
                            visible = uiState.isSelectorExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF141923).copy(alpha = 0.94f))
                                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(20.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "Selecciona un destino:",
                                    fontSize = 12.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                uiState.availableAirports.forEach { airport ->
                                    if (airport.icaoCode != uiState.selectedOrigin.icaoCode) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { viewModel.selectDestination(airport) }
                                                .padding(vertical = 8.dp, horizontal = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FlightLand,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "${airport.city} (${airport.icaoCode})",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = airport.name,
                                                    color = Color.Gray,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ==========================================
            // HUD DE VUELO DE FONDO (Interactivo)
            // ==========================================
            if (uiState.selectedBackgroundFlightId != null) {
                item {
                    val tappedFlight = uiState.backgroundFlights.find { it.id == uiState.selectedBackgroundFlightId }
                    if (tappedFlight != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1E2430).copy(alpha = 0.95f))
                                .border(1.dp, if (tappedFlight.isEmptyLeg) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (tappedFlight.isEmptyLeg) "✨ EMPTY LEG VIP" else "VUELO EN CURSO",
                                        color = if (tappedFlight.isEmptyLeg) Color(0xFFFBBF24) else Color.Gray,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "✕",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        modifier = Modifier.clickable { viewModel.dismissFlightHud() }
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${tappedFlight.origin.icaoCode} ➔ ${tappedFlight.dest.icaoCode}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${tappedFlight.origin.city} to ${tappedFlight.dest.city}",
                                    color = Color.LightGray,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                JetXButton(
                                    text = if (tappedFlight.isEmptyLeg) "Reservar con 65% OFF" else "Ver detalles del vuelo",
                                    onClick = { /* Acción para reservar */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    variant = if (tappedFlight.isEmptyLeg) JetXButtonVariant.PRIMARY else JetXButtonVariant.SECONDARY
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // ==========================================
            // SECCIONES DEL CATÁLOGO VIP (Scroll vertical)
            // ==========================================
            item {
                DestinationsSection(destinations = destinations)
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                VipEventsSection(events = vipEvents)
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                VipServicesSection(services = vipServices)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // ==========================================
        // BARRA DE NAVEGACIÓN FLOTANTE (Fixed Bottom)
        // ==========================================
        JetXFloatingBottomBar(
            selectedTab = currentTab,
            onTabSelected = { currentTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    com.example.jetx.presentation.theme.JetXTheme {
        BookingScreen()
    }
}