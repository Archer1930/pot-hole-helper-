package com.example.data.model

enum class MunicipalDepartment(
    val departmentName: String,
    val jurisdictionType: String,
    val dispatchEmail: String,
    val hotlinePhone: String,
    val targetTurnaround: String
) {
    CITY_DOT_311(
        departmentName = "City Dept of Transportation (DOT) & 311",
        jurisdictionType = "City Avenues & Municipal Streets",
        dispatchEmail = "potholes@metrodot.gov",
        hotlinePhone = "311",
        targetTurnaround = "48 Hours (High Priority for >3\" depth)"
    ),
    STATE_HIGHWAY_DOT(
        departmentName = "State DOT Highway Maintenance Division",
        jurisdictionType = "State Highways, Interstates & Ramps",
        dispatchEmail = "highways@statedot.gov",
        hotlinePhone = "1-800-368-7623",
        targetTurnaround = "24 Hours (Urgent Traffic Safety)"
    ),
    COUNTY_PUBLIC_WORKS(
        departmentName = "County Road & Bridge Commission",
        jurisdictionType = "County Arterials & Rural Roads",
        dispatchEmail = "roadworks@countyroads.gov",
        hotlinePhone = "1-888-268-6897",
        targetTurnaround = "3-5 Business Days"
    )
}

enum class DotReportStatus(val label: String, val badgeColorHex: Long) {
    TICKET_SUBMITTED("Ticket Logged", 0xFF00E5FF),
    DISPATCHED_TO_CREW("Crew Dispatched", 0xFFFFB300),
    COLD_PATCH_IN_PROGRESS("Patch In Progress", 0xFFFFB300),
    PERMANENT_REPAIRED("Repaired & Verified", 0xFF00E676)
}

data class MunicipalReportTicket(
    val ticketId: String,
    val potholeId: String,
    val timestamp: Long,
    val department: MunicipalDepartment,
    val streetAddress: String,
    val crossStreet: String,
    val gpsCoordinates: String,
    val craterDepthInches: Float,
    val lane: LanePosition,
    val hazardType: HazardType,
    val severity: HazardSeverity,
    val status: DotReportStatus,
    val estimatedRepairDate: String,
    val dashcamPhotoDrawable: Int? = null,
    val customPhotoUri: String? = null,
    val driverStatement: String = ""
)
