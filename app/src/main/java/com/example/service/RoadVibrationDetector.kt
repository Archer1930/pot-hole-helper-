package com.example.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

data class VibrationReading(
    val gForceMagnitude: Float = 1.0f,
    val verticalDeltaG: Float = 0.0f,
    val isImpactSpike: Boolean = false,
    val roughnessIndex: Float = 0.2f, // 0.0 (Glass smooth) to 1.0 (Rocky bone-rattling)
    val lastImpactTimestamp: Long = 0L
)

class RoadVibrationDetector(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _vibrationState = MutableStateFlow(VibrationReading())
    val vibrationState: StateFlow<VibrationReading> = _vibrationState.asStateFlow()

    private var gravity = floatArrayOf(0f, 0f, 9.8f)
    private var isListening = false

    fun startListening() {
        if (isListening) return
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        }
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager?.unregisterListener(this)
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // High-pass filter to isolate sudden vertical road shocks from standard 1G gravity
        val alpha = 0.8f
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        val linearZ = event.values[2] - gravity[2]
        val totalDelta = sqrt(
            (event.values[0] - gravity[0]) * (event.values[0] - gravity[0]) +
            (event.values[1] - gravity[1]) * (event.values[1] - gravity[1]) +
            linearZ * linearZ
        ) / 9.8f

        val isImpact = totalDelta > 0.65f // Threshold for road strike
        val currentRoughness = (totalDelta * 1.5f).coerceIn(0.1f, 1.0f)

        _vibrationState.value = VibrationReading(
            gForceMagnitude = 1.0f + totalDelta,
            verticalDeltaG = totalDelta,
            isImpactSpike = isImpact,
            roughnessIndex = currentRoughness,
            lastImpactTimestamp = if (isImpact) System.currentTimeMillis() else _vibrationState.value.lastImpactTimestamp
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun simulateBumpSpike() {
        _vibrationState.value = VibrationReading(
            gForceMagnitude = 2.4f,
            verticalDeltaG = 1.4f,
            isImpactSpike = true,
            roughnessIndex = 0.92f,
            lastImpactTimestamp = System.currentTimeMillis()
        )
    }
}
