package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.HazardSeverity
import com.example.data.model.PotholeHazard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.LipRed
import com.example.ui.theme.ScrapeAmber
import com.example.ui.theme.SmoothGreen
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun RadarCanvasView(
    hazards: List<PotholeHazard>,
    selectedHazard: PotholeHazard?,
    effectiveClearanceInches: Float,
    isDriving: Boolean,
    onHazardSelected: (PotholeHazard) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(hazards) {
                detectTapGestures { tapOffset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val radius = size.width.coerceAtMost(size.height) * 0.44f

                    // Find closest hazard to tap point
                    hazards.forEachIndexed { index, hazard ->
                        val angleRad = Math.toRadians((index * 51.4 - 90.0))
                        val distRatio = 0.28f + (index % 4) * 0.21f
                        val hX = centerX + (radius * distRatio * cos(angleRad)).toFloat()
                        val hY = centerY + (radius * distRatio * sin(angleRad)).toFloat()

                        val distanceToTap = sqrt((tapOffset.x - hX) * (tapOffset.x - hX) + (tapOffset.y - hY) * (tapOffset.y - hY))
                        if (distanceToTap < 70f) {
                            onHazardSelected(hazard)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = size.width.coerceAtMost(size.height) * 0.44f

        // Draw dark radar background
        drawCircle(
            color = Color(0xFF0C121A),
            radius = maxRadius,
            center = Offset(centerX, centerY)
        )

        // Draw concentric distance range rings
        val ringSteps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
        ringSteps.forEach { step ->
            drawCircle(
                color = Color(0x2200E5FF),
                radius = maxRadius * step,
                center = Offset(centerX, centerY),
                style = Stroke(width = 1.5f)
            )
        }

        // Draw Crosshair axes
        drawLine(
            color = Color(0x1F00E5FF),
            start = Offset(centerX - maxRadius, centerY),
            end = Offset(centerX + maxRadius, centerY),
            strokeWidth = 1f
        )
        drawLine(
            color = Color(0x1F00E5FF),
            start = Offset(centerX, centerY - maxRadius),
            end = Offset(centerX, centerY + maxRadius),
            strokeWidth = 1f
        )

        // Draw safe corridor cone (forward navigation path)
        val path = Path().apply {
            moveTo(centerX, centerY)
            lineTo(centerX - maxRadius * 0.45f, centerY - maxRadius)
            lineTo(centerX + maxRadius * 0.45f, centerY - maxRadius)
            close()
        }
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x2600E5FF), Color(0x0500E5FF)),
                startY = centerY - maxRadius,
                endY = centerY
            )
        )

        // Draw Radar sweep line
        val sweepRad = Math.toRadians(sweepAngle.toDouble())
        val sweepEndX = centerX + (maxRadius * cos(sweepRad)).toFloat()
        val sweepEndY = centerY + (maxRadius * sin(sweepRad)).toFloat()

        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(CyberCyan.copy(alpha = 0.9f), CyberCyan.copy(alpha = 0.05f)),
                center = Offset(centerX, centerY),
                radius = maxRadius
            ),
            start = Offset(centerX, centerY),
            end = Offset(sweepEndX, sweepEndY),
            strokeWidth = 2.5f
        )

        // Draw Pothole Hazards on the radar
        hazards.forEachIndexed { index, hazard ->
            val angleRad = Math.toRadians((index * 51.4 - 90.0))
            val distRatio = 0.28f + (index % 4) * 0.21f
            val hX = centerX + (radiusRatio(distRatio, maxRadius) * cos(angleRad)).toFloat()
            val hY = centerY + (radiusRatio(distRatio, maxRadius) * sin(angleRad)).toFloat()

            val hazardColor = when (hazard.severity) {
                HazardSeverity.CATASTROPHIC -> LipRed
                HazardSeverity.SEVERE -> LipRed
                HazardSeverity.MODERATE -> ScrapeAmber
                HazardSeverity.MILD -> SmoothGreen
            }

            val isSelected = selectedHazard?.id == hazard.id

            // Hazard pulse glow
            if (hazard.severity == HazardSeverity.CATASTROPHIC || isSelected) {
                drawCircle(
                    color = hazardColor.copy(alpha = if (isSelected) 0.5f else 0.25f),
                    radius = (if (isSelected) 22f else 16f) * pulseScale,
                    center = Offset(hX, hY)
                )
            }

            // Hazard core dot
            drawCircle(
                color = hazardColor,
                radius = if (isSelected) 9f else 6.5f,
                center = Offset(hX, hY)
            )

            // Selection ring
            if (isSelected) {
                drawCircle(
                    color = CyberCyan,
                    radius = 16f,
                    center = Offset(hX, hY),
                    style = Stroke(width = 2f)
                )
            }
        }

        // Draw Center Vehicle Marker (sport car arrowhead with front splitter glow)
        val carSize = 16f
        val carPath = Path().apply {
            moveTo(centerX, centerY - carSize * 1.5f) // Nose
            lineTo(centerX + carSize, centerY + carSize) // Right rear
            lineTo(centerX, centerY + carSize * 0.6f) // Tail center
            lineTo(centerX - carSize, centerY + carSize) // Left rear
            close()
        }
        drawPath(
            path = carPath,
            color = CyberCyan
        )
        // Vehicle center dot
        drawCircle(
            color = Color.White,
            radius = 3.5f,
            center = Offset(centerX, centerY)
        )
    }
}

private fun radiusRatio(ratio: Float, maxRadius: Float): Float = maxRadius * ratio
