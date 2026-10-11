package com.example.jetx.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jetx.domain.model.DestinationItem
import com.example.jetx.domain.model.EventItem
import com.example.jetx.domain.model.ServiceOptionItem

@Composable
fun DestinationsSection(
    destinations: List<DestinationItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionTitle(title = "Para ti")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(destinations, key = { it.id }) { item ->
                DestinationCard(item = item)
            }
        }
    }
}

@Composable
fun VipEventsSection(
    events: List<EventItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionTitle(title = "Eventos mundiales exclusivos")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(events, key = { it.id }) { event ->
                EventCard(event = event)
            }
        }
    }
}

@Composable
fun VipServicesSection(
    services: List<ServiceOptionItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionTitle(title = "Planifica tu vuelo con tiempo")
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(services, key = { it.id }) { service ->
                ServiceCard(service = service)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
fun JetXCatalogSectionsPreview() {
    val destinations = listOf(DestinationItem("1", "Londres", "Reino Unido", "EGLF", "8h 15m", "Gulfstream G600"))
    val events = listOf(EventItem("e1", "Gran Premio F1 Mónaco", "Aterrizaje en Niza + Transfer en helicóptero", "Mónaco", "FÓRMULA 1"))
    val services = listOf(ServiceOptionItem("s1", "Empty Legs VIP", "Ahorra hasta 65% en vuelos de reposicionamiento vacíos.", "TARIFA SMART"))
    
    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DestinationsSection(destinations = destinations)
        VipEventsSection(events = events)
        VipServicesSection(services = services)
    }
}