package com.example.data.model

enum class Obd2ConnectionState(val label: String, val badgeColorHex: Long) {
    CONNECTED("CAN-Bus Linked", 0xFF00E676),
    CONNECTING("Syncing ECU...", 0xFFFFB300),
    DISCONNECTED("Adapter Standby", 0xFF94A3B8)
}

data class Obd2Telemetry(
    val connectionState: Obd2ConnectionState = Obd2ConnectionState.CONNECTED,
    val deviceName: String = "ScrapeGuard CAN-Link OBD2 (BLE 5.3)",
    val vehicleProtocol: String = "ISO 15765-4 CAN (11-bit 500k baud)",
    val speedMph: Int = 32,
    val engineRpm: Int = 2450,
    val suspensionClearanceMm: Int = 71, // 2.8 inches
    val wheelDropSpikeDetected: Boolean = false,
    val coolantTempF: Int = 195,
    val alternatorVoltage: Float = 14.2f,
    val throttlePercent: Int = 18,
    val isClearanceLaserActive: Boolean = true
) {
    val clearanceInches: Float
        get() = (suspensionClearanceMm / 25.4f * 10).toInt() / 10f
}
