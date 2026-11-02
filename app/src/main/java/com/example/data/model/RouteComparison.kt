package com.example.data.model

data class RouteStep(
    val instruction: String,
    val distanceText: String,
    val isPotholeZone: Boolean = false,
    val warningText: String? = null,
    val safeLaneAdvice: String? = null
)

data class RouteOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val isScrapeGuardSafeRoute: Boolean,
    val estimatedTimeMinutes: Int,
    val distanceMiles: Float,
    val severePotholesCount: Int,
    val moderatePotholesCount: Int,
    val overallScrapeRiskPercent: Int,
    val financialRiskEstimateDollars: Int,
    val roadConditionSummary: String,
    val steps: List<RouteStep>,
    val waypointsQuery: String // For Google Maps intent
)

data class RouteComparison(
    val tripTitle: String,
    val origin: String,
    val destination: String,
    val googleMapsDirectRoute: RouteOption,
    val scrapeGuardDetourRoute: RouteOption,
    val potholeWayLaneGuidanceRoute: RouteOption
) {
    val timeDifferenceMinutes: Int
        get() = scrapeGuardDetourRoute.estimatedTimeMinutes - googleMapsDirectRoute.estimatedTimeMinutes

    val dollarSavings: Int
        get() = (googleMapsDirectRoute.financialRiskEstimateDollars - scrapeGuardDetourRoute.financialRiskEstimateDollars).coerceAtLeast(0)
}
