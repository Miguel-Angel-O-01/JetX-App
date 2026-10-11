package com.example.jetx.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import earth.worldwind.WorldWindow
import earth.worldwind.geom.Offset
import earth.worldwind.geom.Position
import earth.worldwind.layer.RenderableLayer
import earth.worldwind.render.Color as WwColor
import earth.worldwind.render.image.ImageSource
import earth.worldwind.shape.Path
import earth.worldwind.shape.Placemark
import earth.worldwind.shape.PlacemarkAttributes
import earth.worldwind.shape.ShapeAttributes
import kotlin.math.*

@Composable
fun WorldWindMapInternal(
    originLat: Double,
    originLon: Double,
    destLat: Double,
    destLon: Double,
    currentLat: Double,
    currentLon: Double,
    currentBearing: Double,
    currentAltitudeMetres: Double,
    outsideTempCelsius: Double,
    modifier: Modifier = Modifier
) {
    // 1. Cálculos de distancia y tiempo
    val distanceKm = calculateHaversineDistance(originLat, originLon, destLat, destLon)
    val distanceNm = (distanceKm * 0.539957).roundToInt()
    val totalHoursDecimal = (distanceNm.toDouble() / 450.0) + 0.35
    val hours = totalHoursDecimal.toInt()
    val minutes = ((totalHoursDecimal - hours) * 60).roundToInt()
    val flightTimeText = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    val badgeText = "✈  $distanceNm NM • $flightTimeText"

    val context = androidx.compose.ui.platform.LocalContext.current
    val stlRenderer = androidx.compose.runtime.remember(context) {
        com.example.jetx.util.StlRenderer(context, com.example.jetx.R.raw.jetx3d)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WorldWindow(context).apply {
                    engine.layers.addLayer(earth.worldwind.layer.BackgroundLayer())
                    engine.layers.addLayer(earth.worldwind.layer.BlueMarbleLayer())

                    val routeLayer = RenderableLayer("RouteLayer")
                    val badgeLayer = RenderableLayer("Badge3DLayer")
                    engine.layers.addLayer(routeLayer)
                    engine.layers.addLayer(badgeLayer)

                    val centerLat = (originLat + destLat) / 2
                    val centerLon = (originLon + destLon) / 2
                    engine.camera.set(
                        earth.worldwind.geom.Angle.fromDegrees(centerLat), 
                        earth.worldwind.geom.Angle.fromDegrees(centerLon), 
                        17_500_000.0,
                        earth.worldwind.geom.AltitudeMode.ABSOLUTE,
                        earth.worldwind.geom.Angle.fromDegrees(0.0),
                        earth.worldwind.geom.Angle.fromDegrees(0.0),
                        earth.worldwind.geom.Angle.fromDegrees(0.0)
                    )

                    // Control de gestos para priorizar rotación del globo
                    setOnTouchListener { view, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                                view.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                view.parent?.requestDisallowInterceptTouchEvent(false)
                                view.performClick()
                            }
                        }
                        view.onTouchEvent(event)
                        true
                    }
                }
            },
            update = { worldWindow ->
                val routeLayer = worldWindow.engine.layers.firstOrNull { it.displayName == "RouteLayer" } as? RenderableLayer
                val badgeLayer = worldWindow.engine.layers.firstOrNull { it.displayName == "Badge3DLayer" } as? RenderableLayer

                routeLayer?.clearRenderables()
                
                // A1. Ruta completa simulada (línea punteada/translucida)
                val fullRoute = Path(listOf(
                    Position.fromDegrees(originLat, originLon, 0.0),
                    Position.fromDegrees(destLat, destLon, 0.0)
                )).apply {
                    attributes = ShapeAttributes().apply {
                        outlineColor = WwColor(1.0f, 1.0f, 1.0f, 0.35f)
                        outlineWidth = 2.5f
                    }
                }
                routeLayer?.addRenderable(fullRoute)

                // Altitud dinámica para que el jet vuele animado sobre la curvatura
                val altitudeOffset = (distanceKm * 1000.0 * 0.10) + (currentAltitudeMetres * 40.0)
                val planePos = Position.fromDegrees(currentLat, currentLon, altitudeOffset.coerceIn(300_000.0, 1_500_000.0))

                // A2. Tramo recorrido en vivo (línea celeste brillante que finaliza EXACTAMENTE en la punta del jet)
                val traveledRoute = Path(listOf(
                    Position.fromDegrees(originLat, originLon, 0.0),
                    planePos
                )).apply {
                    attributes = ShapeAttributes().apply {
                        outlineColor = WwColor(0.22f, 0.74f, 0.97f, 1.0f)
                        outlineWidth = 5.0f
                    }
                }
                routeLayer?.addRenderable(traveledRoute)

                // B. Limpiar y redibujar elementos móviles
                badgeLayer?.clearRenderables()

                // B1. Texto flotante de la telemetría en vivo (desplazado arriba del jet)
                val badgeBitmap = createTelemetryPillBitmap(badgeText)
                val badgePlacemark = Placemark(Position.fromDegrees(currentLat, currentLon, planePos.altitude + 180_000.0)).apply {
                    attributes = PlacemarkAttributes().apply {
                        imageSource = ImageSource.fromBitmap(badgeBitmap)
                        imageScale = 1.0
                        imageOffset = Offset.bottomCenter()
                        isDrawLeader = false
                    }
                }
                badgeLayer?.addRenderable(badgePlacemark)

                // B2. Renderizar el modelo 3D jetx3d.stl (200x200px) alineado en la punta de la ruta de vuelo
                val jet3dBitmap = stlRenderer.renderToBitmap(
                    size = 230,
                    yawDeg = 0f, 
                    yawOffsetDeg = 0f, 
                    pitchDeg = 0f
                )
                val jetFactory = ImageSource.fromBitmap(jet3dBitmap)

                // =========================================================================
                // AJUSTE DE ÁNGULO Y ROTACIÓN DEL JET 3D SOBRE LA RUTA
                // =========================================================================
                val jetAngleOffset = 180.0 
                val finalRotation = (currentBearing + jetAngleOffset) % 360.0

                val jetPlacemark = Placemark(planePos).apply {
                    attributes = PlacemarkAttributes().apply {
                        imageSource = jetFactory
                        imageScale = 1.30
                        imageOffset = Offset.center()
                        // Rotación nativa del Placemark en WorldWind alineado exactamente con la punta
                        imageRotation = earth.worldwind.geom.Angle.fromDegrees(finalRotation)
                        isDrawLeader = false
                    }
                }
                badgeLayer?.addRenderable(jetPlacemark)

                worldWindow.requestRedraw()
            }
        )
    }
}

/**
 * Dibuja una cápsula flotante VIP de alto contraste para WorldWind
 */
private fun createTelemetryPillBitmap(text: String): Bitmap {
    val density = 3f // Escala para nitidez en pantallas de alta resolución
    val paddingHorizontal = 18f * density
    val paddingVertical = 9f * density
    val fontSize = 13f * density

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        textSize = fontSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    val textBounds = android.graphics.Rect()
    textPaint.getTextBounds(text, 0, text.length, textBounds)

    val width = (textBounds.width() + (paddingHorizontal * 2)).toInt()
    val height = (textBounds.height() + (paddingVertical * 2.2f)).toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val rect = RectF(2f, 2f, width - 2f, height - 2f)
    val cornerRadius = height / 2f

    // 1. Fondo completamente transparente
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.TRANSPARENT
        style = Paint.Style.FILL
    }
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)

    // 2. Texto centrado con sombra
    textPaint.setShadowLayer(5f * density, 0f, 2f * density, android.graphics.Color.BLACK)
    val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
    canvas.drawText(text, width / 2f, textY, textPaint)

    return bitmap
}

private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}