package com.example.data.repository

import com.example.R
import com.example.data.model.DotReportStatus
import com.example.data.model.FreezeThawRisk
import com.example.data.model.HazardSeverity
import com.example.data.model.HazardType
import com.example.data.model.HistoricalHotspot
import com.example.data.model.LanePosition
import com.example.data.model.MunicipalDepartment
import com.example.data.model.MunicipalReportTicket
import com.example.data.model.PotholeHazard
import com.example.data.model.RouteComparison
import com.example.data.model.RouteOption
import com.example.data.model.RouteStep
import com.example.data.model.WeatherCondition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class HazardRepository {

    private val initialHazards = listOf(
        PotholeHazard(
            id = "hz_1",
            streetName = "Grand Avenue",
            crossStreet = "at 8th Street Underpass",
            city = "Central Metro",
            latitude = 37.7749,
            longitude = -122.4194,
            severity = HazardSeverity.CATASTROPHIC,
            type = HazardType.DEEP_POTHOLE,
            lane = LanePosition.CENTER_LANE,
            depthInches = 4.2f,
            reportCount = 89,
            scrapeIncidentsLogged = 34,
            historicalRecurrenceRate = 0.95f,
            freezeThawVulnerability = FreezeThawRisk.EXTREME,
            firstReportedYear = 2021,
            lastVerifiedDaysAgo = 1,
            isHiddenByWaterInRain = true,
            communityNotes = "Crater right in dip after light. Shattered carbon splitter on a lowered RS3. Deep puddle conceals it after rain.",
            dashcamPhotoDrawable = R.drawable.dashcam_photo_crater,
            jurisdiction = MunicipalDepartment.CITY_DOT_311,
            municipalTicketId = "DOT-311-84920"
        ),
        PotholeHazard(
            id = "hz_2",
            streetName = "Industrial Parkway",
            crossStreet = "near Freight Rail Crossing",
            city = "Central Metro",
            latitude = 37.7765,
            longitude = -122.4170,
            severity = HazardSeverity.SEVERE,
            type = HazardType.FROST_HEAVE,
            lane = LanePosition.LEFT_LANE,
            depthInches = 3.4f,
            reportCount = 62,
            scrapeIncidentsLogged = 19,
            historicalRecurrenceRate = 0.91f,
            freezeThawVulnerability = FreezeThawRisk.EXTREME,
            firstReportedYear = 2022,
            lastVerifiedDaysAgo = 2,
            isHiddenByWaterInRain = true,
            communityNotes = "Severe pavement buckle caused by frost expansion underneath asphalt.",
            dashcamPhotoDrawable = null,
            jurisdiction = MunicipalDepartment.COUNTY_PUBLIC_WORKS,
            municipalTicketId = null
        ),
        PotholeHazard(
            id = "hz_3",
            streetName = "Market Boulevard",
            crossStreet = "Corner of 4th Avenue",
            city = "Central Metro",
            latitude = 37.7788,
            longitude = -122.4145,
            severity = HazardSeverity.SEVERE,
            type = HazardType.RAISED_MANHOLE,
            lane = LanePosition.RIGHT_LANE,
            depthInches = 3.1f,
            reportCount = 47,
            scrapeIncidentsLogged = 22,
            historicalRecurrenceRate = 0.88f,
            freezeThawVulnerability = FreezeThawRisk.HIGH,
            firstReportedYear = 2020,
            lastVerifiedDaysAgo = 3,
            isHiddenByWaterInRain = false,
            communityNotes = "Sunken road around utility cover creates an aggressive 3\" iron edge. Dangerous for low-profile 30-series tires.",
            dashcamPhotoDrawable = R.drawable.dashcam_photo_manhole,
            jurisdiction = MunicipalDepartment.CITY_DOT_311,
            municipalTicketId = "DOT-311-79114"
        ),
        PotholeHazard(
            id = "hz_4",
            streetName = "Oakridge Expressway (Off-Ramp)",
            crossStreet = "Exit 12B ramp apron",
            city = "Central Metro",
            latitude = 37.7812,
            longitude = -122.4120,
            severity = HazardSeverity.MODERATE,
            type = HazardType.STEEP_GUTTER_DIP,
            lane = LanePosition.FULL_SPAN,
            depthInches = 2.4f,
            reportCount = 38,
            scrapeIncidentsLogged = 15,
            historicalRecurrenceRate = 0.76f,
            freezeThawVulnerability = FreezeThawRisk.MODERATE,
            firstReportedYear = 2023,
            lastVerifiedDaysAgo = 5,
            isHiddenByWaterInRain = false,
            communityNotes = "Vicious compression dip at bridge joiner. Take at 5 mph or angle car.",
            dashcamPhotoDrawable = null,
            jurisdiction = MunicipalDepartment.STATE_HIGHWAY_DOT,
            municipalTicketId = null
        ),
        PotholeHazard(
            id = "hz_5",
            streetName = "River Road North",
            crossStreet = "between 14th & 16th Way",
            city = "Central Metro",
            latitude = 37.7840,
            longitude = -122.4090,
            severity = HazardSeverity.CATASTROPHIC,
            type = HazardType.DEEP_POTHOLE,
            lane = LanePosition.LEFT_LANE,
            depthInches = 4.5f,
            reportCount = 112,
            scrapeIncidentsLogged = 41,
            historicalRecurrenceRate = 0.98f,
            freezeThawVulnerability = FreezeThawRisk.EXTREME,
            firstReportedYear = 2019,
            lastVerifiedDaysAgo = 1,
            isHiddenByWaterInRain = true,
            communityNotes = "Water line leaks below road surface; freeze-thaw cycle destroys cold patches within 48 hours.",
            dashcamPhotoDrawable = R.drawable.dashcam_photo_crater,
            jurisdiction = MunicipalDepartment.CITY_DOT_311,
            municipalTicketId = "DOT-311-91044"
        ),
        PotholeHazard(
            id = "hz_6",
            streetName = "Pinecrest Avenue",
            crossStreet = "near College Plaza",
            city = "Central Metro",
            latitude = 37.7725,
            longitude = -122.4215,
            severity = HazardSeverity.MODERATE,
            type = HazardType.CONSTRUCTION_TRENCH,
            lane = LanePosition.CENTER_LANE,
            depthInches = 2.2f,
            reportCount = 29,
            scrapeIncidentsLogged = 8,
            historicalRecurrenceRate = 0.65f,
            freezeThawVulnerability = FreezeThawRisk.MODERATE,
            firstReportedYear = 2024,
            lastVerifiedDaysAgo = 4,
            isHiddenByWaterInRain = true,
            communityNotes = "Temporary asphalt patch sunk 2 inches below grade.",
            dashcamPhotoDrawable = null,
            jurisdiction = MunicipalDepartment.CITY_DOT_311,
            municipalTicketId = null
        ),
        PotholeHazard(
            id = "hz_7",
            streetName = "Broad Street Viaduct",
            crossStreet = "Eastbound span joint 3",
            city = "Central Metro",
            latitude = 37.7865,
            longitude = -122.4060,
            severity = HazardSeverity.SEVERE,
            type = HazardType.BRIDGE_EXPANSION_GAP,
            lane = LanePosition.RIGHT_LANE,
            depthInches = 3.3f,
            reportCount = 53,
            scrapeIncidentsLogged = 17,
            historicalRecurrenceRate = 0.89f,
            freezeThawVulnerability = FreezeThawRisk.HIGH,
            firstReportedYear = 2021,
            lastVerifiedDaysAgo = 2,
            isHiddenByWaterInRain = false,
            communityNotes = "Exposed jagged steel teeth of bridge expansion plate.",
            dashcamPhotoDrawable = null,
            jurisdiction = MunicipalDepartment.STATE_HIGHWAY_DOT,
            municipalTicketId = null
        )
    )

    private val initialTickets = listOf(
        MunicipalReportTicket(
            ticketId = "DOT-311-84920",
            potholeId = "hz_1",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            department = MunicipalDepartment.CITY_DOT_311,
            streetAddress = "Grand Avenue at 8th Street Underpass",
            crossStreet = "8th Street Underpass",
            gpsCoordinates = "37.7749° N, 122.4194° W",
            craterDepthInches = 4.2f,
            lane = LanePosition.CENTER_LANE,
            hazardType = HazardType.DEEP_POTHOLE,
            severity = HazardSeverity.CATASTROPHIC,
            status = DotReportStatus.DISPATCHED_TO_CREW,
            estimatedRepairDate = "Tomorrow, 8:00 AM",
            dashcamPhotoDrawable = R.drawable.dashcam_photo_crater,
            driverStatement = "Severe 4.2\" crater in depression after traffic signal. Lowered vehicles bottoming out on oil pan; dashcam footage submitted."
        ),
        MunicipalReportTicket(
            ticketId = "DOT-311-79114",
            potholeId = "hz_3",
            timestamp = System.currentTimeMillis() - 86400000L * 5,
            department = MunicipalDepartment.CITY_DOT_311,
            streetAddress = "Market Boulevard Corner of 4th Avenue",
            crossStreet = "4th Avenue",
            gpsCoordinates = "37.7788° N, 122.4145° W",
            craterDepthInches = 3.1f,
            lane = LanePosition.RIGHT_LANE,
            hazardType = HazardType.RAISED_MANHOLE,
            severity = HazardSeverity.SEVERE,
            status = DotReportStatus.COLD_PATCH_IN_PROGRESS,
            estimatedRepairDate = "Today, 4:00 PM",
            dashcamPhotoDrawable = R.drawable.dashcam_photo_manhole,
            driverStatement = "Protruding iron casting with broken surround. Sliced tire sidewall on sports coupe."
        ),
        MunicipalReportTicket(
            ticketId = "DOT-311-91044",
            potholeId = "hz_5",
            timestamp = System.currentTimeMillis() - 86400000L * 1,
            department = MunicipalDepartment.CITY_DOT_311,
            streetAddress = "River Road North between 14th & 16th Way",
            crossStreet = "14th & 16th Way",
            gpsCoordinates = "37.7840° N, 122.4090° W",
            craterDepthInches = 4.5f,
            lane = LanePosition.LEFT_LANE,
            hazardType = HazardType.DEEP_POTHOLE,
            severity = HazardSeverity.CATASTROPHIC,
            status = DotReportStatus.TICKET_SUBMITTED,
            estimatedRepairDate = "Pending Crew Scheduling",
            dashcamPhotoDrawable = R.drawable.dashcam_photo_crater,
            driverStatement = "Recurring winter freeze-thaw blowout. Water ponding hides crater."
        )
    )

    private val initialHotspots = listOf(
        HistoricalHotspot(
            corridorName = "Grand Avenue Industrial Basin",
            segmentLimits = "From 6th St to 12th St Corridor (1.4 mi)",
            recurrenceRatePercent = 95,
            consecutiveYearsPersisting = 5,
            averagePotholeDepthInches = 3.9f,
            chronicCause = "Heavy commercial logistics traffic combined with shallow sub-base drainage and extreme freeze-thaw hydraulic action.",
            winterFreezeThawSpikes = 42,
            scrapeReportsThisMonth = 34,
            freezeThawRiskLevel = FreezeThawRisk.EXTREME,
            recommendedSafeAlternative = "Freshly repaved Skyline Arterial via Bay Bypass",
            predictiveWarningText = "CRITICAL: Past 5 years show 95% winter patch failure rate within 6 days of temperature swinging below 32°F."
        ),
        HistoricalHotspot(
            corridorName = "River Road Sub-surface Canal",
            segmentLimits = "Mile Marker 2.1 to 3.8 along North Bank",
            recurrenceRatePercent = 98,
            consecutiveYearsPersisting = 6,
            averagePotholeDepthInches = 4.3f,
            chronicCause = "High groundwater table seeps into asphalt pores; freezing water expands by 9% volume, shattering the surface binder.",
            winterFreezeThawSpikes = 58,
            scrapeReportsThisMonth = 41,
            freezeThawRiskLevel = FreezeThawRisk.EXTREME,
            recommendedSafeAlternative = "Highland Crest Parkway (Elevated Concrete Deck)",
            predictiveWarningText = "HIGH DANGER: Potholes on this segment frequently submerge under 3\" puddles during rain/melt, blinding drivers."
        ),
        HistoricalHotspot(
            corridorName = "Market Boulevard Tram Corridor",
            segmentLimits = "Civic Center to Embarcadero (2.0 mi)",
            recurrenceRatePercent = 88,
            consecutiveYearsPersisting = 4,
            averagePotholeDepthInches = 3.2f,
            chronicCause = "Track vibration and recessed iron utility castings create repeating sharp asphalt drop-offs.",
            winterFreezeThawSpikes = 28,
            scrapeReportsThisMonth = 22,
            freezeThawRiskLevel = FreezeThawRisk.HIGH,
            recommendedSafeAlternative = "Folsom Repaved Transitway",
            predictiveWarningText = "ADVISORY: Recurrent edge fractures adjacent to rail tracks; low-profile rims risk severe sidewall cuts."
        ),
        HistoricalHotspot(
            corridorName = "Broad Street Overpass Apron",
            segmentLimits = "Junction 14 Interchange",
            recurrenceRatePercent = 84,
            consecutiveYearsPersisting = 3,
            averagePotholeDepthInches = 2.9f,
            chronicCause = "Thermal expansion differentials between steel bridge truss and approach asphalt.",
            winterFreezeThawSpikes = 19,
            scrapeReportsThisMonth = 17,
            freezeThawRiskLevel = FreezeThawRisk.HIGH,
            recommendedSafeAlternative = "Lower Deck Surface Blvd",
            predictiveWarningText = "MODERATE: Expansion gap widens dramatically in freezing temps, creating abrupt 3\" metallic lip."
        )
    )

    private val _hazards = MutableStateFlow(initialHazards)
    val hazards: StateFlow<List<PotholeHazard>> = _hazards.asStateFlow()

    private val _tickets = MutableStateFlow(initialTickets)
    val municipalTickets: StateFlow<List<MunicipalReportTicket>> = _tickets.asStateFlow()

    private val _historicalHotspots = MutableStateFlow(initialHotspots)
    val historicalHotspots: StateFlow<List<HistoricalHotspot>> = _historicalHotspots.asStateFlow()

    private val _currentWeather = MutableStateFlow(WeatherCondition.FREEZE_THAW_POST_WINTER)
    val currentWeather: StateFlow<WeatherCondition> = _currentWeather.asStateFlow()

    fun updateWeather(weather: WeatherCondition) {
        _currentWeather.value = weather
    }

    fun reportHazard(
        street: String,
        crossStreet: String,
        depthInches: Float,
        severity: HazardSeverity,
        type: HazardType,
        lane: LanePosition,
        notes: String,
        photoDrawable: Int? = null,
        photoUri: String? = null,
        department: MunicipalDepartment = MunicipalDepartment.CITY_DOT_311
    ): PotholeHazard {
        val newHazard = PotholeHazard(
            id = "hz_${UUID.randomUUID().toString().take(6)}",
            streetName = street.ifBlank { "Reported Road Segment" },
            crossStreet = crossStreet.ifBlank { "Near Current GPS Point" },
            latitude = 37.7770 + (Math.random() - 0.5) * 0.01,
            longitude = -122.4160 + (Math.random() - 0.5) * 0.01,
            severity = severity,
            type = type,
            lane = lane,
            depthInches = depthInches,
            reportCount = 1,
            scrapeIncidentsLogged = if (severity == HazardSeverity.SEVERE || severity == HazardSeverity.CATASTROPHIC) 1 else 0,
            historicalRecurrenceRate = 0.5f,
            freezeThawVulnerability = FreezeThawRisk.HIGH,
            firstReportedYear = 2026,
            lastVerifiedDaysAgo = 0,
            isHiddenByWaterInRain = true,
            communityNotes = notes.ifBlank { "Driver documented with dashcam evidence." },
            dashcamPhotoDrawable = photoDrawable,
            customPhotoUri = photoUri,
            jurisdiction = department,
            municipalTicketId = null
        )
        _hazards.value = listOf(newHazard) + _hazards.value
        return newHazard
    }

    fun submitMunicipalTicket(
        hazard: PotholeHazard,
        department: MunicipalDepartment,
        driverStatement: String,
        photoDrawable: Int?,
        photoUri: String?
    ): MunicipalReportTicket {
        val ticketId = "DOT-311-${(10000..99999).random()}"
        val newTicket = MunicipalReportTicket(
            ticketId = ticketId,
            potholeId = hazard.id,
            timestamp = System.currentTimeMillis(),
            department = department,
            streetAddress = "${hazard.streetName} at ${hazard.crossStreet}",
            crossStreet = hazard.crossStreet,
            gpsCoordinates = "${String.format("%.4f", hazard.latitude)}° N, ${String.format("%.4f", hazard.longitude)}° W",
            craterDepthInches = hazard.depthInches,
            lane = hazard.lane,
            hazardType = hazard.type,
            severity = hazard.severity,
            status = DotReportStatus.TICKET_SUBMITTED,
            estimatedRepairDate = "Within 48 Hours (${department.targetTurnaround})",
            dashcamPhotoDrawable = photoDrawable ?: hazard.dashcamPhotoDrawable,
            customPhotoUri = photoUri ?: hazard.customPhotoUri,
            driverStatement = driverStatement.ifBlank { "Hazard verified by lowered car radar. Immediate repair requested to prevent oil pan punctures and wheel damage." }
        )

        // Update list of tickets
        _tickets.value = listOf(newTicket) + _tickets.value

        // Update hazard with ticket ID
        _hazards.value = _hazards.value.map { h ->
            if (h.id == hazard.id) h.copy(municipalTicketId = ticketId, jurisdiction = department)
            else h
        }

        return newTicket
    }

    fun getRouteComparison(effectiveClearance: Float): RouteComparison {
        val isFreeze = _currentWeather.value.isFreezeThawActive
        val googleSteps = listOf(
            RouteStep("Head north on Grand Avenue toward 8th St", "0.4 mi", false),
            RouteStep("CRITICAL WARNING: 4.2\" crater across center lane at 8th St underpass!", "0.1 mi", true, "Deep water-filled pothole - Extreme scrape hazard!", "Stay tightly in far right shoulder"),
            RouteStep("Continue on Industrial Parkway past railroad crossing", "0.6 mi", false),
            RouteStep("SEVERE HAZARD: 3.4\" Frost heave buckling left lane!", "0.1 mi", true, "Asphalt wave - Carbon splitter destruction zone", "Move immediately to center lane"),
            RouteStep("Turn right onto Market Blvd", "0.8 mi", false),
            RouteStep("MODERATE HAZARD: Protruding 3.1\" cast iron manhole cover", "0.2 mi", true, "Sunken surrounding asphalt rim trap", "Straddle left wheels"),
            RouteStep("Arrive at Destination", "0.2 mi", false)
        )

        val scrapeGuardSteps = listOf(
            RouteStep("Depart via Repaved Skyline Arterial (Smooth Grade A Asphalt)", "1.1 mi", false),
            RouteStep("Keep left onto Bay Vista Bypass (Fresh 2025 resurfacing, zero potholes)", "0.9 mi", false),
            RouteStep("Turn slightly right onto Folsom Expressway (Level Grade)", "0.7 mi", false),
            RouteStep("Smooth approach ramp into North Hills Cars & Coffee parking lot", "0.3 mi", false),
            RouteStep("Safely arrived - Zero scrapes, Splitter & Rims 100% intact", "0.1 mi", false)
        )

        val googleDirect = RouteOption(
            id = "gmaps_default",
            title = "Google Maps Default (Fastest on Paper)",
            subtitle = "Direct route via Grand Ave & Industrial Pkwy",
            isScrapeGuardSafeRoute = false,
            estimatedTimeMinutes = 14,
            distanceMiles = 2.4f,
            severePotholesCount = if (isFreeze) 5 else 4,
            moderatePotholesCount = 3,
            overallScrapeRiskPercent = if (effectiveClearance < 3.2f) 96 else 78,
            financialRiskEstimateDollars = if (effectiveClearance < 3.0f) 1850 else 950,
            roadConditionSummary = "Riddled with 4 deep frost craters and sunken manhole collars. 95% scrape probability for lowered cars.",
            steps = googleSteps,
            waypointsQuery = "Grand+Avenue+and+8th+Street"
        )

        val scrapeGuardDetour = RouteOption(
            id = "scrapeguard_safe",
            title = "ScrapeGuard Low-Clearance Safe Detour",
            subtitle = "Rerouted via Skyline Arterial & Bay Vista Bypass",
            isScrapeGuardSafeRoute = true,
            estimatedTimeMinutes = 17,
            distanceMiles = 3.0f,
            severePotholesCount = 0,
            moderatePotholesCount = 0,
            overallScrapeRiskPercent = 2,
            financialRiskEstimateDollars = 0,
            roadConditionSummary = "100% freshly paved arterial corridor. Smooth grade, no sharp transition dips or exposed utility castings.",
            steps = scrapeGuardSteps,
            waypointsQuery = "Skyline+Arterial+via+Bay+Vista+Bypass"
        )

        val potholeWaySteps = listOf(
            RouteStep("Head north on Grand Avenue in FAR RIGHT LANE", "0.4 mi", false, null, "Stay in Right Lane - Center Lane has 4.2\" crater ahead"),
            RouteStep("MAINTAIN RIGHT LANE: Passing 4.2\" Crater in Center Lane", "0.1 mi", true, "Center Lane Crater Avoided!", "Clearance safe on right asphalt"),
            RouteStep("SHIFT TO CENTER LANE on Industrial Pkwy before rail crossing", "0.6 mi", false, null, "Move smoothly to Center Lane - Left Lane buckled"),
            RouteStep("PASS RAIL CROSSING IN CENTER LANE: Left Lane has 3.4\" Frost Heave", "0.1 mi", true, "Left Lane Buckle Avoided!", "Straddle center crown"),
            RouteStep("Turn right onto Market Blvd into LEFT LANE", "0.8 mi", false, null, "Hug Left Lane - Right Lane has 3.1\" raised iron manhole"),
            RouteStep("PASS 4TH AVE IN LEFT LANE: 3.1\" Iron Manhole safely on your right", "0.2 mi", true, "Manhole Trap Avoided!", "All clear ahead"),
            RouteStep("Arrive at Destination safely via The Pothole Way - Zero Damage!", "0.2 mi", false)
        )

        val potholeWayRoute = RouteOption(
            id = "pothole_way_lane_guidance",
            title = "The Pothole Way (Active Lane Guidance)",
            subtitle = "Direct 14 min route with turn-by-turn evasive lane maneuvers",
            isScrapeGuardSafeRoute = true,
            estimatedTimeMinutes = 14,
            distanceMiles = 2.4f,
            severePotholesCount = if (isFreeze) 5 else 4,
            moderatePotholesCount = 3,
            overallScrapeRiskPercent = 6,
            financialRiskEstimateDollars = 0,
            roadConditionSummary = "Fastest direct road navigated with lane-by-lane evasion. Turn-by-turn prompts steer you around craters without leaving the direct route.",
            steps = potholeWaySteps,
            waypointsQuery = "Grand+Avenue+and+8th+Street"
        )

        return RouteComparison(
            tripTitle = "Downtown Marina to North Hills Cars & Coffee",
            origin = "Downtown Marina",
            destination = "North Hills Cars & Coffee",
            googleMapsDirectRoute = googleDirect,
            scrapeGuardDetourRoute = scrapeGuardDetour,
            potholeWayLaneGuidanceRoute = potholeWayRoute
        )
    }
}
