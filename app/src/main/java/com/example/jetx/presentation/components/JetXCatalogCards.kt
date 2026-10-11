package com.example.jetx.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetx.domain.model.DestinationItem
import com.example.jetx.domain.model.EventItem
import com.example.jetx.domain.model.ServiceOptionItem

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun DestinationCard(
    item: DestinationItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(220.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
            // Aquí puedes agregar la imagen de fondo con Image(...) cuando agregues los Drawables
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(14.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "${item.city}, ${item.country}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.icaoCode} • ${item.estimatedFlightHours}",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = item.recommendedJet,
                color = Color.White.copy(alpha = 0.60f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun EventCard(
    event: EventItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(260.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1C1917), Color(0xFF292524))
                )
            )
            // Aquí puedes agregar la imagen temática (F1, Davos, Arte)
            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF59E0B).copy(alpha = 0.20f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = event.tag,
                    color = Color(0xFFFBBF24),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = event.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.subtitle,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ServiceCard(
    service: ServiceOptionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(200.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF111827))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = service.tag,
                color = Color(0xFF10B981),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Column {
                Text(
                    text = service.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.description,
                    color = Color.White.copy(alpha = 0.60f),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF050505)
@Composable
fun JetXCatalogCardsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle(title = "Destinos Destacados")
        DestinationCard(
            item = DestinationItem("1", "Londres", "Reino Unido", "EGLF", "8h 15m", "Gulfstream G600")
        )
        EventCard(
            event = EventItem("e1", "Gran Premio F1 Mónaco", "Aterrizaje en Niza + Transfer", "Mónaco", "FÓRMULA 1")
        )
        ServiceCard(
            service = ServiceOptionItem("s1", "Empty Legs VIP", "Ahorra hasta 65% en vuelos de reposicionamiento", "TARIFA SMART")
        )
    }
}