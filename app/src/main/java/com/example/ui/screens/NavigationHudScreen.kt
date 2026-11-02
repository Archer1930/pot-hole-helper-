package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Obd2ConnectionState
import com.example.data.model.Obd2Telemetry
import com.example.data.model.PotholeHazard
import com.example.data.model.VehicleProfile
import com.example.data.model.WeatherCondition
import com.example.service.AlertLevel
import com.example.service.DriveTelemetry
import com.example.service.DrivingMode
import com.example.service.VibrationReading
import com.example.ui.components.HazardDetailCard
import com.example.ui.components.StreetRadarMapView
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkAsphalt
import com.example.ui.theme.FrostIce
import com.example.ui.theme.LipRed
import com.example.ui.theme.ScrapeAmber
import com.example.ui.theme.SmoothGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NavigationHudScreen(
    hazards: List<PotholeHazard>,
    selectedHazard: PotholeHazard?,
    vehicle: VehicleProfile,
    weather: WeatherCondition,
    telemetry: DriveTelemetry,
    vibration: VibrationReading,
    obd2Telemetry: Obd2Telemetry,
    onHazardSelected: (PotholeHazard?) -> Unit,
    onToggleAirLift: () -> Unit,
    onStartSimulation: () -> Unit,
    onStopSimulation: () -> Unit,
    onOpenWeatherDialog: () -> Unit,
    onOpenReportDialog: () -> Unit,
    onNavigateSafeDetour: () -> Unit,
    onLaunchGoogleMaps: () -> Unit,
    onTestBump: () -> Unit,
    onViewDashcam: (PotholeHazard) -> Unit = {},
    onReportToDot: (PotholeHazard) -> Unit = {},
    onOpenGpsHardwareModal: () -> Unit = {},
    onOpenObd2Modal: () -> Unit = {},
    onOpenCameraVisionModal: () -> Unit = {},
    onOpenThemePicker: () -> Unit = {},
    onSelectLane: (com.example.data.model.LanePosition) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDriving = telemetry.drivingMode == DrivingMode.SIMULATED_DRIVE
    val isCritical = telemetry.alertLevel == AlertLevel.CRITICAL_SCRAPE_DANGER

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkAsphalt)
    ) {
        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            // Top Cockpit Header
            TopCockpitHeader(
                vehicle = vehicle,
                weather = weather,
                obd2Telemetry = obd2Telemetry,
                onToggleAirLift = onToggleAirLift,
                onOpenWeatherDialog = onOpenWeatherDialog,
                onOpenObd2Modal = onOpenObd2Modal,
                onOpenCameraVisionModal = onOpenCameraVisionModal,
                onOpenThemePicker = onOpenThemePicker
            )

            // Dynamic Alert Banner (when approaching a hazard)
            AnimatedVisibility(
                visible = telemetry.alertLevel != AlertLevel.ALL_CLEAR || isCritical,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                HazardAlertBanner(
                    telemetry = telemetry,
                    onRerouteClicked = onNavigateSafeDetour
                )
            }

            // The Pothole Way: Real-Time Active 3-Lane Highway Guidance Bar
            ActiveLaneGuidanceCard(
                telemetry = telemetry,
                onSelectLane = onSelectLane
            )

            // Radar Visualizer Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                StreetRadarMapView(
                    hazards = hazards,
                    selectedHazard = selectedHazard,
                    effectiveClearanceInches = vehicle.effectiveClearanceInches,
                    isDriving = isDriving,
                    driveDistanceProgress = telemetry.distanceToNearestFeet,
                    onHazardSelected = onHazardSelected,
                    onOpenGpsHardwareModal = onOpenGpsHardwareModal
                )

                // Overlaid Hazard Detail Card if selected
                if (selectedHazard != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp)
                    ) {
                        HazardDetailCard(
                            hazard = selectedHazard,
                            vehicle = vehicle,
                            onDismiss = { onHazardSelected(null) },
                            onDetourClicked = onNavigateSafeDetour,
                            onViewDashcam = onViewDashcam,
                            onReportToDot = onReportToDot
                        )
                    }
                }
            }

            // Bottom Driving Telemetry Bar
            BottomTelemetryControlBar(
                telemetry = telemetry,
                vibration = vibration,
                vehicle = vehicle,
                isDriving = isDriving,
                onStartSimulation = onStartSimulation,
                onStopSimulation = onStopSimulation,
                onLaunchGoogleMaps = onLaunchGoogleMaps,
                onTestBump = onTestBump
            )
        }

        // Report Pothole FAB
        FloatingActionButton(
            onClick = onOpenReportDialog,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 140.dp)
                .testTag("report_pothole_fab"),
            containerColor = ScrapeAmber,
            contentColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.AddAlert,
                contentDescription = "Report Pothole Hazard"
            )
        }
    }
}

@Composable
private fun TopCockpitHeader(
    vehicle: VehicleProfile,
    weather: WeatherCondition,
    obd2Telemetry: Obd2Telemetry,
    onToggleAirLift: () -> Unit,
    onOpenWeatherDialog: () -> Unit,
    onOpenObd2Modal: () -> Unit,
    onOpenCameraVisionModal: () -> Unit,
    onOpenThemePicker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F151F))
            .border(1.dp, CardBorder)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Upper Row: Vehicle & Clearance Info + Advance Warning Threshold Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = vehicle.vehicleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CLEARANCE: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = "${String.format("%.1f", vehicle.effectiveClearanceInches)}\"",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (vehicle.isLiftActive) SmoothGreen else CyberCyan
                    )
                    if (vehicle.suspensionType.canLift) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (vehicle.isLiftActive) SmoothGreen.copy(alpha = 0.2f) else CardDark)
                                .border(1.dp, if (vehicle.isLiftActive) SmoothGreen else CardBorder, RoundedCornerShape(6.dp))
                                .clickable { onToggleAirLift() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("air_lift_toggle")
                        ) {
                            Text(
                                text = if (vehicle.isLiftActive) "LIFT +1.8\" ACTIVE" else "AIR LIFT READY",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (vehicle.isLiftActive) SmoothGreen else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 0.5-Mile Radar Advance Range Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF003844))
                    .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "RADAR: 0.5 MI ADVANCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lower Row: Interactive Controls (Weather, OBD2 CAN-bus, AI Vision Cam, Theme Customizer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weather Risk Pill
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (weather.isFreezeThawActive) FrostIce.copy(alpha = 0.2f) else Color(0xFF16202E))
                    .border(1.dp, if (weather.isFreezeThawActive) FrostIce else CardBorder, RoundedCornerShape(14.dp))
                    .clickable { onOpenWeatherDialog() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("weather_risk_pill")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (weather.isFreezeThawActive) Icons.Outlined.AcUnit else Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = if (weather.isFreezeThawActive) FrostIce else ScrapeAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (weather.isFreezeThawActive) "FREEZE (2.4x)" else weather.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (weather.isFreezeThawActive) FrostIce else TextPrimary,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            // OBD2 Adapter Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (obd2Telemetry.connectionState == Obd2ConnectionState.CONNECTED) SmoothGreen.copy(alpha = 0.15f) else Color(0xFF16202E))
                    .border(
                        1.dp,
                        if (obd2Telemetry.connectionState == Obd2ConnectionState.CONNECTED) SmoothGreen else CardBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onOpenObd2Modal() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("obd2_adapter_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cable,
                        contentDescription = "OBD2 CAN-Bus",
                        tint = if (obd2Telemetry.connectionState == Obd2ConnectionState.CONNECTED) SmoothGreen else TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (obd2Telemetry.connectionState == Obd2ConnectionState.CONNECTED) "OBD2 LIVE" else "OBD2 STANDBY",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (obd2Telemetry.connectionState == Obd2ConnectionState.CONNECTED) SmoothGreen else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // AI Vision Road Scanner Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberCyan.copy(alpha = 0.15f))
                    .border(1.dp, CyberCyan, RoundedCornerShape(14.dp))
                    .clickable { onOpenCameraVisionModal() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("camera_vision_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "AI Camera Scanner",
                        tint = CyberCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI CAM",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Theme Palette Customizer Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF16202E))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .clickable { onOpenThemePicker() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("theme_picker_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme Colors",
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "THEME",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HazardAlertBanner(
    telemetry: DriveTelemetry,
    onRerouteClicked: () -> Unit
) {
    val isCritical = telemetry.alertLevel == AlertLevel.CRITICAL_SCRAPE_DANGER
    val isAdvanceHalfMile = telemetry.alertLevel == AlertLevel.HALF_MILE_ADVANCE_WARNING
    val bannerColor = when {
        isCritical -> LipRed
        isAdvanceHalfMile -> CyberCyan
        else -> ScrapeAmber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("hazard_alert_banner"),
        colors = CardDefaults.cardColors(
            containerColor = bannerColor.copy(alpha = 0.18f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, bannerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = bannerColor,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = when {
                            isCritical -> "⚠️ CRITICAL POTHOLE IN ${telemetry.distanceToNearestFeet} FT!"
                            isAdvanceHalfMile -> "0.5 MI ADVANCE RADAR: HAZARD IN ${telemetry.distanceToNearestFeet} FT"
                            else -> "CAUTION: ROAD HAZARD IN ${telemetry.distanceToNearestFeet} FT"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = bannerColor
                    )
                    Text(
                        text = telemetry.evasiveLaneAdvice,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary
                    )
                }
            }

            Button(
                onClick = onRerouteClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = bannerColor,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("banner_reroute_button")
            ) {
                Text(
                    text = "DODGE",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ActiveLaneGuidanceCard(
    telemetry: DriveTelemetry,
    onSelectLane: (com.example.data.model.LanePosition) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("active_lane_guidance_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D141F)),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AltRoute,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "THE POTHOLE WAY: LANE GUIDANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = if (telemetry.distanceToNearestFeet > 0) "${telemetry.distanceToNearestFeet} FT AHEAD" else "PATH CLEAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3-Lane Highway Visualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Lane 1: Left Lane
                LanePill(
                    laneName = "LANE 1 (LEFT)",
                    isSafe = telemetry.leftLaneSafe,
                    isRecommended = telemetry.recommendedLane == com.example.data.model.LanePosition.LEFT_LANE,
                    onClick = { onSelectLane(com.example.data.model.LanePosition.LEFT_LANE) },
                    modifier = Modifier.weight(1f)
                )

                // Lane 2: Center Lane
                LanePill(
                    laneName = "LANE 2 (CENTER)",
                    isSafe = telemetry.centerLaneSafe,
                    isRecommended = telemetry.recommendedLane == com.example.data.model.LanePosition.CENTER_LANE,
                    onClick = { onSelectLane(com.example.data.model.LanePosition.CENTER_LANE) },
                    modifier = Modifier.weight(1f)
                )

                // Lane 3: Right Lane
                LanePill(
                    laneName = "LANE 3 (RIGHT)",
                    isSafe = telemetry.rightLaneSafe,
                    isRecommended = telemetry.recommendedLane == com.example.data.model.LanePosition.RIGHT_LANE,
                    onClick = { onSelectLane(com.example.data.model.LanePosition.RIGHT_LANE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = telemetry.laneGuidanceInstruction,
                style = MaterialTheme.typography.bodySmall,
                color = if (telemetry.centerLaneSafe && telemetry.leftLaneSafe && telemetry.rightLaneSafe) SmoothGreen else ScrapeAmber,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun LanePill(
    laneName: String,
    isSafe: Boolean,
    isRecommended: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isRecommended -> SmoothGreen
        !isSafe -> LipRed
        else -> CardBorder
    }
    val bgColor = when {
        isRecommended -> SmoothGreen.copy(alpha = 0.18f)
        !isSafe -> LipRed.copy(alpha = 0.2f)
        else -> DarkAsphalt
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(if (isRecommended || !isSafe) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = laneName,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    !isSafe -> "⚠️ DANGER"
                    isRecommended -> "✅ DODGE HERE"
                    else -> "CLEAR"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (!isSafe) LipRed else if (isRecommended) SmoothGreen else TextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BottomTelemetryControlBar(
    telemetry: DriveTelemetry,
    vibration: VibrationReading,
    vehicle: VehicleProfile,
    isDriving: Boolean,
    onStartSimulation: () -> Unit,
    onStopSimulation: () -> Unit,
    onLaunchGoogleMaps: () -> Unit,
    onTestBump: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Speedometer & G-Force Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speedometer
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${telemetry.speedMph}",
                        style = MaterialTheme.typography.displayMedium,
                        color = CyberCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MPH",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Road Roughness & Impact Meter
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { onTestBump() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (vibration.isImpactSpike) {
                            Text(
                                text = "ROAD STRIKE!",
                                style = MaterialTheme.typography.labelSmall,
                                color = LipRed,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "ROAD G-FORCE: ${String.format("%.2f", vibration.gForceMagnitude)}G",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (vibration.isImpactSpike) LipRed else TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { vibration.roughnessIndex },
                        modifier = Modifier
                            .width(110.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (vibration.roughnessIndex > 0.6f) LipRed else CyberCyan,
                        trackColor = Color(0xFF0F151F)
                    )
                    Text(
                        text = "Tap to test suspension spike",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Driving Simulation Toggle
                Button(
                    onClick = if (isDriving) onStopSimulation else onStartSimulation,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("toggle_drive_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDriving) LipRed.copy(alpha = 0.25f) else CyberCyan.copy(alpha = 0.2f),
                        contentColor = if (isDriving) LipRed else CyberCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDriving) LipRed else CyberCyan)
                ) {
                    Icon(
                        imageVector = if (isDriving) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDriving) "STOP DRIVE" else "SIMULATE DRIVE",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Google Maps Safe Navigation Action
                Button(
                    onClick = onLaunchGoogleMaps,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_google_maps_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SmoothGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MAPS NAV",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
