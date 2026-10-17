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
    backgroundFlights: List<com.example.jetx.presentation.screen.booking.FlightRoute> = emptyList(),
    onFlightTapped: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val stlRenderer = androidx.compose.runtime.remember(context) {
        com.example.jetx.util.StlRenderer(context, com.example.jetx.R.raw.jetx3d)
    }

    // Pre-renderizamos los modelos 3D sólidos para optimización
    val activeJetBitmap = androidx.compose.runtime.remember(stlRenderer) {
        stlRenderer.renderToBitmap(size = 200, yawDeg = 0f, yawOffsetDeg = 0f, pitchDeg = 0f)
    }
    
    val staticJetBitmap = androidx.compose.runtime.remember(stlRenderer) {
        stlRenderer.renderToBitmap(size = 140, yawDeg = 0f, yawOffsetDeg = 0f, pitchDeg = 0f)
    }

    // 1. Cálculos de distancia y tiempo
    val distanceKm = calculateHaversineDistance(originLat, originLon, destLat, destLon)
    val distanceNm = (distanceKm * 0.539957).roundToInt()
    val totalHoursDecimal = (distanceNm.toDouble() / 450.0) + 0.35
    val hours = totalHoursDecimal.toInt()
    val minutes = ((totalHoursDecimal - hours) * 60).roundToInt()
    val flightTimeText = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    val badgeText = "✈  $distanceNm NM • $flightTimeText"

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

                    // Capa de Atmósfera (Día/Noche automático)
                    engine.layers.addLayer(earth.worldwind.layer.atmosphere.AtmosphereLayer())

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

                    // Control de gestos para priorizar rotación y selección (Picking)
                    setOnTouchListener { view, event ->
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                                view.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            MotionEvent.ACTION_UP -> {
                                view.parent?.requestDisallowInterceptTouchEvent(false)
                                view.performClick()
                                
                                // Ejecutamos la lógica de Picking 3D (Rayo de selección hacia el avión en WorldWind 2.x)
                                val pickList = pick(event.x, event.y)
                                val topPicked = pickList.topPickedObject
                                val pickedObject = topPicked?.userObject
                                if (pickedObject is Placemark && pickedObject.displayName?.startsWith("bg_") == true) {
                                    val flightId = pickedObject.displayName!!.removePrefix("bg_")
                                    onFlightTapped(flightId)
                                    
                                    // Auto-Cámara: Movimiento suave hacia el avión seleccionado
                                    engine.camera.set(
                                        pickedObject.position.latitude,
                                        pickedObject.position.longitude,
                                        3_000_000.0, // Acercamos la cámara al jet
                                        earth.worldwind.geom.AltitudeMode.ABSOLUTE,
                                        earth.worldwind.geom.Angle.fromDegrees(0.0),
                                        earth.worldwind.geom.Angle.fromDegrees(0.0),
                                        earth.worldwind.geom.Angle.fromDegrees(0.0)
                                    )
                                }
                            }
                            MotionEvent.ACTION_CANCEL -> {
                                view.parent?.requestDisallowInterceptTouchEvent(false)
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
                val badgePlacemark = Placemark(Position.fromDegrees(currentLat, currentLon, planePos.altitude + 150_000.0)).apply {
                    attributes = PlacemarkAttributes().apply {
                        imageSource = ImageSource.fromBitmap(badgeBitmap)
                        imageScale = 1.0
                        imageOffset = Offset.bottomCenter()
                        isDrawLeader = false
                    }
                }
                badgeLayer?.addRenderable(badgePlacemark)

                // B2. Avión 3D del vuelo activo (Animado)
                val jetFactory = ImageSource.fromBitmap(activeJetBitmap)
                val jetAngleOffset = 180.0 
                val finalRotation = (currentBearing + jetAngleOffset) % 360.0

                val jetPlacemark = Placemark(planePos).apply {
                    attributes = PlacemarkAttributes().apply {
                        imageSource = jetFactory
                        imageScale = 1.30
                        imageOffset = Offset.center()
                        imageRotation = earth.worldwind.geom.Angle.fromDegrees(finalRotation)
                        isDrawLeader = false
                    }
                }
                badgeLayer?.addRenderable(jetPlacemark)

                // =========================================================================
                // C. RENDERIZAR VUELOS DE FONDO ESTÁTICOS (Catálogo VIP Activo Mundial)
                // =========================================================================
                backgroundFlights.forEach { bgFlight ->
                    // Calculamos la posición del avión estático en base a su progreso simulado
                    val (bLat, bLon) = interpolateGeodesic(
                        bgFlight.origin.lat, bgFlight.origin.lon, 
                        bgFlight.dest.lat, bgFlight.dest.lon, 
                        bgFlight.progress
                    )
                    
                    val bgBearing = calculateBearing(
                        bgFlight.origin.lat, bgFlight.origin.lon, 
                        bgFlight.dest.lat, bgFlight.dest.lon
                    )

                    // C1. Trazar ruta tenue
                    val bgRoute = Path(listOf(
                        Position.fromDegrees(bgFlight.origin.lat, bgFlight.origin.lon, 0.0),
                        Position.fromDegrees(bgFlight.dest.lat, bgFlight.dest.lon, 0.0)
                    )).apply {
                        attributes = ShapeAttributes().apply {
                            // Si es Empty Leg brilla en naranja suave, si no, se ve tenue (Ghost Mode)
                            outlineColor = if (bgFlight.isEmptyLeg) {
                                WwColor(0.96f, 0.62f, 0.04f, 0.85f)
                            } else {
                                WwColor(1.0f, 1.0f, 1.0f, 0.15f)
                            }
                            outlineWidth = if (bgFlight.isEmptyLeg) 3.0f else 1.8f
                        }
                    }
                    routeLayer?.addRenderable(bgRoute)

                    // C2. Añadir Jet estático 
                    val bgJetPlacemark = Placemark(Position.fromDegrees(bLat, bLon, 380_000.0)).apply {
                        displayName = "bg_${bgFlight.id}" // Asignamos ID para el Picking 3D
                        attributes = PlacemarkAttributes().apply {
                            imageSource = ImageSource.fromBitmap(staticJetBitmap)
                            imageScale = 1.0
                            imageOffset = Offset.center()
                            imageRotation = earth.worldwind.geom.Angle.fromDegrees((bgBearing + 180.0) % 360.0)
                            // Ghost mode si no es un vuelo vacio
                            imageColor = if (bgFlight.isEmptyLeg) WwColor(1f, 1f, 1f, 1f) else WwColor(1f, 1f, 1f, 0.4f)
                            isDrawLeader = false
                        }
                    }
                    badgeLayer?.addRenderable(bgJetPlacemark)
                }

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