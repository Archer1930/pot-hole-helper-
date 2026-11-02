package com.example.data.model

enum class HazardSeverity(val title: String, val minDepth: Float, val riskDescription: String) {
    MILD("Mild Ripple / Dip", 0.8f, "Noticeable bump, safe for >3.5\" clearance"),
    MODERATE("Lip Scraper", 1.8f, "Severe front lip scrape risk for <3.2\" clearance"),
    SEVERE("Rim Bender / Splitter Destroyer", 3.0f, "High probability of bent wheel rim or cracked carbon splitter"),
    CATASTROPHIC("Oil Pan / Subframe Killer", 4.2f, "Extreme depth crater! Will bottom out chassis or shatter oil pan")
}

enum class HazardType(val displayName: String, val iconName: String) {
    DEEP_POTHOLE("Deep Asphalt Crater", "pothole"),
    FROST_HEAVE("Freeze-Thaw Frost Ridge", "ac_unit"),
    RAISED_MANHOLE("Protruding Sewer Casting", "disc_full"),
    STEEP_GUTTER_DIP("Severe Drainage Gutter", "water"),
    CONSTRUCTION_TRENCH("Unpaved Utility Trench", "warning"),
    BRIDGE_EXPANSION_GAP("Jagged Expansion Joint", "linear_scale")
}

enum class LanePosition(val label: String, val evasiveAction: String) {
    LEFT_LANE("Left Lane", "Hug Right Edge or Move Center"),
    CENTER_LANE("Center Lane", "Take Left or Right Lane"),
    RIGHT_LANE("Right Lane", "Straddle Left or Move Center"),
    FULL_SPAN("Spans Entire Width", "Full Deceleration or Reroute Required")
}

enum class FreezeThawRisk(val label: String, val factor: Float, val detail: String) {
    LOW("Stable Sub-base", 1.0f, "Minimal seasonal cracking observed"),
    MODERATE("Periodic Fatigue", 1.4f, "Cracking observed after heavy rain"),
    HIGH("Frequent Re-opener", 2.2f, "Consistently re-breaks every spring thaw"),
    EXTREME("Chronic Collapse Zone", 3.5f, "Asphalt disintegrates after sub-zero freeze-thaw cycles")
}

data class PotholeHazard(
    val id: String,
    val streetName: String,
    val crossStreet: String,
    val city: String = "Metro Area",
    val latitude: Double,
    val longitude: Double,
    val severity: HazardSeverity,
    val type: HazardType,
    val lane: LanePosition,
    val depthInches: Float,
    val reportCount: Int,
    val scrapeIncidentsLogged: Int,
    val historicalRecurrenceRate: Float, // 0.0 - 1.0 (e.g. 0.92 means reappears 92% of seasons)
    val freezeThawVulnerability: FreezeThawRisk,
    val firstReportedYear: Int,
    val lastVerifiedDaysAgo: Int,
    val isHiddenByWaterInRain: Boolean = true,
    val communityNotes: String = "",
    val dashcamPhotoDrawable: Int? = null,
    val customPhotoUri: String? = null,
    val jurisdiction: MunicipalDepartment = MunicipalDepartment.CITY_DOT_311,
    val municipalTicketId: String? = null
) {
    fun calculateScrapeRisk(effectiveClearance: Float, weatherMultiplier: Float = 1.0f): Float {
        // Clearance deficit vs pothole depth
        val clearanceMargin = effectiveClearance - depthInches
        val baseRisk = when {
            clearanceMargin <= 0.0f -> 1.0f
            clearanceMargin < 0.8f -> 0.85f
            clearanceMargin < 1.5f -> 0.55f
            clearanceMargin < 2.2f -> 0.25f
            else -> 0.05f
        }
        val adjustedRisk = (baseRisk * weatherMultiplier * freezeThawVulnerability.factor)
            .coerceIn(0.0f, 1.0f)
        return adjustedRisk
    }
}
