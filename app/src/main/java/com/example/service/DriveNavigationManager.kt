package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.model.LanePosition
import com.example.data.model.PotholeHazard
import com.example.data.model.VehicleProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class DrivingMode {
    STANDBY,
    ACTIVE_RADAR,
    SIMULATED_DRIVE
}

enum class AlertLevel {
    ALL_CLEAR,
    HALF_MILE_ADVANCE_WARNING,
    CAUTION_AHEAD,
    CRITICAL_SCRAPE_DANGER
}

data class DriveTelemetry(
    val drivingMode: DrivingMode = DrivingMode.STANDBY,
    val speedMph: Int = 0,
    val nearestHazard: PotholeHazard? = null,
    val distanceToNearestFeet: Int = 0,
    val warningThresholdFeet: Int = 2640, // Exactly 0.5 Mile ahead!
    val alertLevel: AlertLevel = AlertLevel.ALL_CLEAR,
    val currentStreet: String = "Grand Avenue",
    val evasiveLaneAdvice: String = "All Clear - Lane Center Safe",
    val estimatedDamageAtRisk: Int = 0,
    val isRerouteRecommended: Boolean = false,
    val isFloatingHudActive: Boolean = false,
    val isPotholeWayMode: Boolean = true, // The Pothole Way - with active lane dodge guidance!
    val activeLane: LanePosition = LanePosition.RIGHT_LANE,
    val recommendedLane: LanePosition = LanePosition.RIGHT_LANE,
    val leftLaneSafe: Boolean = true,
    val centerLaneSafe: Boolean = false,
    val rightLaneSafe: Boolean = true,
    val laneGuidanceInstruction: String = "LANE 3 (RIGHT) SAFE - AVOIDS 4.2\" CRATER IN CENTER"
) {
    val distanceFormatted: String
        get() = when {
            distanceToNearestFeet >= 2640 -> "0.5 mi (${distanceToNearestFeet} ft)"
            distanceToNearestFeet >= 1000 -> "${String.format("%.1f", distanceToNearestFeet / 5280f)} mi (${distanceToNearestFeet} ft)"
            else -> "${distanceToNearestFeet} ft"
        }
}

class DriveNavigationManager(private val context: Context) {

    private val _telemetry = MutableStateFlow(DriveTelemetry())
    val telemetry: StateFlow<DriveTelemetry> = _telemetry.asStateFlow()

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun setWarningThreshold(feet: Int) {
        _telemetry.value = _telemetry.value.copy(warningThresholdFeet = feet)
    }

    fun startDriveSimulation(hazards: List<PotholeHazard>, vehicle: VehicleProfile) {
        simulationJob?.cancel()
        _telemetry.value = _telemetry.value.copy(
            drivingMode = DrivingMode.SIMULATED_DRIVE,
            speedMph = 45,
            currentStreet = "Grand Avenue"
        )

        simulationJob = scope.launch {
            var distance = 2750 // Starts just above 0.5 mile (2,640 ft) to show advance warning trigger
            val targetHazard = hazards.firstOrNull() ?: return@launch

            while (isActive) {
                delay(650)
                distance -= 75

                if (distance <= 0) {
                    // Passed hazard successfully!
                    triggerScrapeSavedCelebration()
                    distance = 2800 // Reset to 0.5 mile out for the next obstacle
                }

                val threshold = _telemetry.value.warningThresholdFeet // 2640 ft (0.5 mi)
                val alert = when {
                    distance < 300 -> AlertLevel.CRITICAL_SCRAPE_DANGER
                    distance < 1200 -> AlertLevel.CAUTION_AHEAD
                    distance <= threshold -> AlertLevel.HALF_MILE_ADVANCE_WARNING
                    else -> AlertLevel.ALL_CLEAR
                }

                if (alert == AlertLevel.CRITICAL_SCRAPE_DANGER) {
                    triggerHapticWarning()
                } else if (alert == AlertLevel.HALF_MILE_ADVANCE_WARNING && distance in (threshold - 100)..threshold) {
                    // Chime once when crossing into the 0.5 mile perimeter
                    triggerAdvanceAlertChime()
                }

                val damageAtRisk = if (alert != AlertLevel.ALL_CLEAR) {
                    vehicle.frontLipType.replaceCost + vehicle.wheelProfile.rimRepairCost
                } else 0

                val (recLane, lSafe, cSafe, rSafe, guidance) = when (targetHazard.lane) {
                    com.example.data.model.LanePosition.CENTER_LANE -> Quintuple(
                        LanePosition.RIGHT_LANE, true, false, true, "LANE 3 (RIGHT) SAFE - AVOIDS 4.2\" CENTER CRATER"
                    )
                    com.example.data.model.LanePosition.LEFT_LANE -> Quintuple(
                        LanePosition.CENTER_LANE, false, true, true, "LANE 2 (CENTER) SAFE - AVOIDS 3.4\" FROST HEAVE"
                    )
                    com.example.data.model.LanePosition.RIGHT_LANE -> Quintuple(
                        LanePosition.LEFT_LANE, true, true, false, "LANE 1 (LEFT) SAFE - AVOIDS 3.1\" SUNKEN MANHOLE"
                    )
                    com.example.data.model.LanePosition.FULL_SPAN -> Quintuple(
                        LanePosition.CENTER_LANE, false, false, false, "FULL WIDTH DIP - DECELERATE & ANGLE TIRES"
                    )
                }

                _telemetry.value = _telemetry.value.copy(
                    speedMph = if (distance < 250) 18 else if (distance < 1000) 32 else 45,
                    nearestHazard = targetHazard,
                    distanceToNearestFeet = distance,
                    alertLevel = alert,
                    evasiveLaneAdvice = if (alert != AlertLevel.ALL_CLEAR) guidance else "Path Clear - All Lanes Open",
                    estimatedDamageAtRisk = damageAtRisk,
                    isRerouteRecommended = alert == AlertLevel.CRITICAL_SCRAPE_DANGER || alert == AlertLevel.CAUTION_AHEAD,
                    recommendedLane = recLane,
                    leftLaneSafe = lSafe,
                    centerLaneSafe = cSafe,
                    rightLaneSafe = rSafe,
                    laneGuidanceInstruction = guidance
                )
            }
        }
    }

    fun togglePotholeWayMode() {
        _telemetry.value = _telemetry.value.copy(isPotholeWayMode = !_telemetry.value.isPotholeWayMode)
    }

    fun selectLane(lane: LanePosition) {
        _telemetry.value = _telemetry.value.copy(activeLane = lane)
    }

    fun stopDrive() {
        simulationJob?.cancel()
        simulationJob = null
        _telemetry.value = _telemetry.value.copy(
            drivingMode = DrivingMode.STANDBY,
            speedMph = 0,
            nearestHazard = null,
            distanceToNearestFeet = 0,
            alertLevel = AlertLevel.ALL_CLEAR,
            evasiveLaneAdvice = "Radar Standby - Ready to Drive"
        )
    }

    fun toggleFloatingHud() {
        _telemetry.value = _telemetry.value.copy(
            isFloatingHudActive = !_telemetry.value.isFloatingHudActive
        )
    }

    private data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

    private fun triggerAdvanceAlertChime() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(80, 100)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(80)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticWarning() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(140, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(140)
            }
        } catch (_: Exception) {}
    }

    private fun triggerScrapeSavedCelebration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 80, 80, 160)
                val amplitudes = intArrayOf(0, 150, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        } catch (_: Exception) {}
    }

    fun launchGoogleMapsNavigation(destinationQuery: String) {
        try {
            val gmmIntentUri = Uri.parse("google.navigation:q=${Uri.encode(destinationQuery)}&avoid=t")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(destinationQuery)}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://maps.google.com/?q=${Uri.encode(destinationQuery)}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }
}
