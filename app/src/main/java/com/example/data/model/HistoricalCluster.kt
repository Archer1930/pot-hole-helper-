package com.example.data.model

data class HistoricalHotspot(
    val corridorName: String,
    val segmentLimits: String,
    val recurrenceRatePercent: Int, // e.g. 94%
    val consecutiveYearsPersisting: Int,
    val averagePotholeDepthInches: Float,
    val chronicCause: String, // e.g. "Aging water main sub-base leak & frost expansion"
    val winterFreezeThawSpikes: Int, // e.g. 24 new reports per winter
    val scrapeReportsThisMonth: Int,
    val freezeThawRiskLevel: FreezeThawRisk,
    val recommendedSafeAlternative: String,
    val predictiveWarningText: String
)

enum class WeatherCondition(
    val title: String,
    val tempDescription: String,
    val hazardMultiplier: Float,
    val isFreezeThawActive: Boolean,
    val visualIcon: String
) {
    DRY_MODERATE(
        "Dry & Clear",
        "68°F / 20°C - Dry Pavement",
        1.0f,
        false,
        "wb_sunny"
    ),
    FREEZE_THAW_POST_WINTER(
        "Active Freeze-Thaw Cycle",
        "31°F - 39°F (Oscillating across freezing)",
        2.4f,
        true,
        "ac_unit"
    ),
    HEAVY_RAIN_FLOODED(
        "Heavy Downpour (Hidden Craters)",
        "54°F - Puddles Camouflage Deep Potholes",
        1.8f,
        false,
        "water_drop"
    ),
    MELTING_SLUSH(
        "Snowmelt & Thermal Shock",
        "34°F - Runoff eroding fissure edges",
        2.1f,
        true,
        "severe_cold"
    )
}
