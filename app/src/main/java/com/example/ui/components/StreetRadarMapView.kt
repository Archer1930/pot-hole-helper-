package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HazardSeverity
import com.example.data.model.PotholeHazard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkAsphalt
import com.example.ui.theme.LipRed
import com.example.ui.theme.ScrapeAmber
import com.example.ui.theme.SmoothGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class MapLayerStyle {
    NAVIGATION_STREETS,
    ASPHALT_QUALITY_HEATMAP,
    SATELLITE_RADAR
}

@Composable
fun StreetRadarMapView(
    hazards: List<PotholeHazard>,
    selectedHazard: PotholeHazard?,
    effectiveClearanceInches: Float,
    isDriving: Boolean,
    driveDistanceProgress: Int, // countdown feet from simulation
    onHazardSelected: (PotholeHazard) -> Unit,
    onOpenGpsHardwareModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mapZoom by remember { mutableFloatStateOf(1.0f) }
    var currentLayer by remember { mutableStateOf(MapLayerStyle.NAVIGATION_STREETS) }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HazardPulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Main Custom Canvas Map
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(hazards, mapZoom) {
                    detectTapGestures { tapOffset ->
                        val width = size.width.toFloat()
                        val height = size.height.toFloat()

                        // Coordinates mapping for potholes on the vector street grid
                        hazards.forEach { hazard ->
                            val (hx, hy) = getHazardScreenCoordinates(hazard.id, width, height, mapZoom)
                            val dist = sqrt((tapOffset.x - hx) * (tapOffset.x - hx) + (tapOffset.y - hy) * (tapOffset.y - hy))
                            if (dist < 65f) {
                                onHazardSelected(hazard)
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // 1. Map Base Canvas (Dark Google Maps Navy Asphalt)
            drawRect(
                color = when (currentLayer) {
                    MapLayerStyle.NAVIGATION_STREETS -> Color(0xFF0F151F)
                    MapLayerStyle.ASPHALT_QUALITY_HEATMAP -> Color(0xFF0A0F17)
                    MapLayerStyle.SATELLITE_RADAR -> Color(0xFF080D14)
                }
            )

            // 2. City Blocks & Urban Land Parcels
            val blockPaint = Color(0xFF141C28)
            val parkPaint = Color(0xFF10231D)
            val riverPaint = Color(0xFF0C1B26)

            // Water feature (Metro River along top-right)
            val riverPath = Path().apply {
                moveTo(width * 0.65f, 0f)
                cubicTo(
                    width * 0.70f, height * 0.25f,
                    width * 0.85f, height * 0.35f,
                    width, height * 0.40f
                )
                lineTo(width, 0f)
                close()
            }
            drawPath(path = riverPath, color = riverPaint)

            // Urban Parcels / City Blocks
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(width * 0.06f, height * 0.08f),
                size = Size(width * 0.38f, height * 0.22f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = parkPaint, // Civic Park
                topLeft = Offset(width * 0.06f, height * 0.36f),
                size = Size(width * 0.38f, height * 0.25f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(width * 0.06f, height * 0.67f),
                size = Size(width * 0.38f, height * 0.26f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(width * 0.52f, height * 0.45f),
                size = Size(width * 0.42f, height * 0.22f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = blockPaint,
                topLeft = Offset(width * 0.52f, height * 0.73f),
                size = Size(width * 0.42f, height * 0.20f),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // 3. Vector Road Network (Google Maps Street Structure)
            val defaultRoadColor = Color(0xFF222C3D)
            val smoothRoadColor = if (currentLayer == MapLayerStyle.ASPHALT_QUALITY_HEATMAP) Color(0xFF00E676).copy(alpha = 0.3f) else Color(0xFF1D3236)
            val badRoadColor = if (currentLayer == MapLayerStyle.ASPHALT_QUALITY_HEATMAP) Color(0xFFFF2D55).copy(alpha = 0.3f) else Color(0xFF332029)

            // Cross Streets (Horizontal)
            // 8th Street Underpass (y = 0.32)
            drawRect(
                color = badRoadColor,
                topLeft = Offset(0f, height * 0.31f),
                size = Size(width, 42f * mapZoom)
            )
            // Market Boulevard (y = 0.63)
            drawRect(
                color = defaultRoadColor,
                topLeft = Offset(0f, height * 0.62f),
                size = Size(width, 46f * mapZoom)
            )

            // Arterials (Vertical)
            // Grand Avenue (Center Primary Arterial, x = 0.46)
            val grandAveWidth = 54f * mapZoom
            drawRect(
                color = badRoadColor, // Severe Pothole Zone
                topLeft = Offset(width * 0.46f - grandAveWidth / 2, 0f),
                size = Size(grandAveWidth, height)
            )

            // Skyline Arterial / Bay Vista Bypass (Detour corridor on right, x = 0.88)
            val skylineWidth = 48f * mapZoom
            drawRect(
                color = smoothRoadColor, // Smooth repaved
                topLeft = Offset(width * 0.88f - skylineWidth / 2, 0f),
                size = Size(skylineWidth, height)
            )

            // Road Divider Dashed Lines
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 18f), 0f)
            // Grand Ave centerline
            drawLine(
                color = Color(0x66FFFFFF),
                start = Offset(width * 0.46f, 0f),
                end = Offset(width * 0.46f, height),
                strokeWidth = 2f,
                pathEffect = dashEffect
            )
            // Market Blvd centerline
            drawLine(
                color = Color(0x66FFFFFF),
                start = Offset(0f, height * 0.64f),
                end = Offset(width, height * 0.64f),
                strokeWidth = 2f,
                pathEffect = dashEffect
            )
            // Skyline Arterial centerline (Bright Green safe route)
            drawLine(
                color = SmoothGreen.copy(alpha = 0.8f),
                start = Offset(width * 0.88f, 0f),
                end = Offset(width * 0.88f, height),
                strokeWidth = 2.5f,
                pathEffect = dashEffect
            )

            // 4. Smooth Safe Route vs Danger Route Polylines
            // Danger Route line (Red gradient along Grand Ave)
            drawLine(
                color = LipRed.copy(alpha = 0.7f),
                start = Offset(width * 0.46f - 8f, height * 0.85f),
                end = Offset(width * 0.46f - 8f, height * 0.20f),
                strokeWidth = 5f
            )

            // ScrapeGuard Safe Detour Route line (Cyan / Green detour line connecting via Market to Skyline)
            val safeDetourPath = Path().apply {
                moveTo(width * 0.46f + 8f, height * 0.85f)
                lineTo(width * 0.46f + 8f, height * 0.64f + 8f) // Turn on Market Blvd
                lineTo(width * 0.88f, height * 0.64f + 8f) // Turn north on Skyline Arterial
                lineTo(width * 0.88f, height * 0.15f) // Smooth arrival
            }
            drawPath(
                path = safeDetourPath,
                color = SmoothGreen,
                style = Stroke(width = 6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 12f), 0f))
            )

            // 5. Street Labels (Native text on canvas)
            val labelPaint = Paint().apply {
                color = android.graphics.Color.argb(220, 240, 244, 248)
                textSize = 28f * mapZoom
                isFakeBoldText = true
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
            val subLabelPaint = Paint().apply {
                color = android.graphics.Color.argb(160, 148, 163, 184)
                textSize = 20f * mapZoom
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            }
            val smoothLabelPaint = Paint().apply {
                color = android.graphics.Color.argb(255, 0, 230, 118)
                textSize = 22f * mapZoom
                isFakeBoldText = true
            }

            drawContext.canvas.nativeCanvas.apply {
                save()
                // Grand Ave label (vertical)
                rotate(-90f, width * 0.43f, height * 0.50f)
                drawText("GRAND AVENUE (POTHOLE RISK)", width * 0.43f, height * 0.50f, labelPaint)
                restore()

                // Market Blvd label
                drawText("MARKET BLVD", width * 0.08f, height * 0.61f, labelPaint)

                // 8th St Underpass
                drawText("8TH ST UNDERPASS", width * 0.08f, height * 0.30f, labelPaint)

                // Skyline Arterial (Smooth)
                save()
                rotate(-90f, width * 0.85f, height * 0.50f)
                drawText("SKYLINE ARTERIAL [SMOOTH GRADE A]", width * 0.85f, height * 0.50f, smoothLabelPaint)
                restore()

                // Civic Park Label
                drawText("CIVIC PARK", width * 0.14f, height * 0.48f, subLabelPaint)
            }

            // 6. Radar Sweep Overlay across the Street Map
            val sweepRadius = width.coerceAtMost(height) * 0.55f * mapZoom
            val sweepRad = Math.toRadians(sweepAngle.toDouble())
            val sweepEndX = centerX + (sweepRadius * cos(sweepRad)).toFloat()
            val sweepEndY = centerY + (sweepRadius * sin(sweepRad)).toFloat()

            // Translucent radar range circles over the city
            listOf(0.4f, 0.7f, 1.0f).forEach { ratio ->
                drawCircle(
                    color = CyberCyan.copy(alpha = 0.12f),
                    radius = sweepRadius * ratio,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.2f)
                )
            }

            // Rotating sweep beam
            drawLine(
                brush = Brush.radialGradient(
                    colors = listOf(CyberCyan.copy(alpha = 0.75f), CyberCyan.copy(alpha = 0.02f)),
                    center = Offset(centerX, centerY),
                    radius = sweepRadius
                ),
                start = Offset(centerX, centerY),
                end = Offset(sweepEndX, sweepEndY),
                strokeWidth = 3f
            )

            // 7. Pothole Hazards Plotted Accurately on the Street Grid
            hazards.forEach { hazard ->
                val (hx, hy) = getHazardScreenCoordinates(hazard.id, width, height, mapZoom)
                val hazardColor = when (hazard.severity) {
                    HazardSeverity.CATASTROPHIC, HazardSeverity.SEVERE -> LipRed
                    HazardSeverity.MODERATE -> ScrapeAmber
                    HazardSeverity.MILD -> SmoothGreen
                }
                val isSelected = selectedHazard?.id == hazard.id

                // Pulse aura
                drawCircle(
                    color = hazardColor.copy(alpha = if (isSelected) 0.55f else 0.28f),
                    radius = (if (isSelected) 26f else 18f) * pulseScale,
                    center = Offset(hx, hy)
                )

                // Outer boundary
                drawCircle(
                    color = Color.Black,
                    radius = if (isSelected) 12f else 9f,
                    center = Offset(hx, hy)
                )

                // Hazard Core
                drawCircle(
                    color = hazardColor,
                    radius = if (isSelected) 9f else 7f,
                    center = Offset(hx, hy)
                )

                // Depth badge next to hazard
                val depthPaint = Paint().apply {
                    color = hazardColor.toArgb()
                    textSize = 18f
                    isFakeBoldText = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "${hazard.depthInches}\"",
                    hx + 12f,
                    hy - 8f,
                    depthPaint
                )
            }

            // 8. Vehicle Position & Movement
            // When driving simulation is active, calculate vehicle position along Grand Ave moving toward 8th St
            val carY = if (isDriving) {
                // Animate from bottom (height * 0.85) toward 8th St (height * 0.33)
                val normalizedDist = (driveDistanceProgress / 1000f).coerceIn(0f, 1f)
                height * 0.33f + (height * 0.50f * normalizedDist)
            } else {
                height * 0.78f
            }
            val carX = width * 0.46f // On Grand Ave center lane

            // Headlight illumination beam cone forward
            val lightPath = Path().apply {
                moveTo(carX, carY)
                lineTo(carX - 55f, carY - 140f)
                lineTo(carX + 55f, carY - 140f)
                close()
            }
            drawPath(
                path = lightPath,
                brush = Brush.verticalGradient(
                    colors = listOf(CyberCyan.copy(alpha = 0.25f), Color.Transparent),
                    startY = carY - 140f,
                    endY = carY
                )
            )

            // Vehicle Marker (Aerodynamic sports car icon)
            val carSize = 18f
            val carPath = Path().apply {
                moveTo(carX, carY - carSize * 1.5f) // Nose
                lineTo(carX + carSize, carY + carSize) // Right rear
                lineTo(carX, carY + carSize * 0.6f) // Tail center
                lineTo(carX - carSize, carY + carSize) // Left rear
                close()
            }
            drawPath(path = carPath, color = CyberCyan)
            drawCircle(color = Color.White, radius = 4f, center = Offset(carX, carY))
        }

        // Map Control Floating Tools (Zoom in / out, layers, ProLink GPS status)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ScrapeGuard ProLink GPS Hardware Status Badge
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CyberCyan, RoundedCornerShape(20.dp))
                    .clickable { onOpenGpsHardwareModal() },
                color = CardDark.copy(alpha = 0.92f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SmoothGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PROLINK GPS SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Layer Switcher (Streets vs Asphalt Quality Heatmap)
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable {
                        currentLayer = when (currentLayer) {
                            MapLayerStyle.NAVIGATION_STREETS -> MapLayerStyle.ASPHALT_QUALITY_HEATMAP
                            MapLayerStyle.ASPHALT_QUALITY_HEATMAP -> MapLayerStyle.SATELLITE_RADAR
                            MapLayerStyle.SATELLITE_RADAR -> MapLayerStyle.NAVIGATION_STREETS
                        }
                    },
                color = CardDark.copy(alpha = 0.9f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Switch Map Layer",
                        tint = if (currentLayer == MapLayerStyle.ASPHALT_QUALITY_HEATMAP) SmoothGreen else CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Zoom In
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable { mapZoom = (mapZoom + 0.15f).coerceAtMost(1.6f) },
                color = CardDark.copy(alpha = 0.9f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
            }

            // Zoom Out
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable { mapZoom = (mapZoom - 0.15f).coerceAtLeast(0.75f) },
                color = CardDark.copy(alpha = 0.9f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Map Legend Bottom Pill (explaining Green = Smooth vs Red = Craters)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 10.dp, start = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xDD0C131D))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SmoothGreen))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Fresh Paved", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LipRed))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pothole Hazard", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = TextPrimary)
        }
    }
}

// Maps specific hazards to coordinates on our vector road network
private fun getHazardScreenCoordinates(id: String, width: Float, height: Float, zoom: Float): Pair<Float, Float> {
    return when (id) {
        "hz_1" -> Pair(width * 0.46f, height * 0.33f) // Grand Ave at 8th St Underpass
        "hz_2" -> Pair(width * 0.46f - 14f, height * 0.48f) // Industrial Pkwy rail junction
        "hz_3" -> Pair(width * 0.32f, height * 0.63f) // Market Blvd Corner of 4th
        "hz_4" -> Pair(width * 0.72f, height * 0.63f) // Oakridge ramp joiner
        "hz_5" -> Pair(width * 0.46f + 12f, height * 0.18f) // River Road North
        "hz_6" -> Pair(width * 0.22f, height * 0.78f) // Pinecrest near College Plaza
        "hz_7" -> Pair(width * 0.62f, height * 0.32f) // Broad Street Viaduct
        else -> Pair(width * 0.46f, height * 0.50f)
    }
}
