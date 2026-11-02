package com.example.data.model

enum class SuspensionType(val label: String, val canLift: Boolean) {
    STATIC_COILOVERS("Static Coilovers", false),
    AIR_SUSPENSION("Air Suspension (Bags)", true),
    LOWERING_SPRINGS("Lowering Springs", false),
    HYDRAULIC_CUP_KIT("Stance Cup Kit", true),
    OEM_SPORT("OEM Sport Low", false)
}

enum class FrontLipType(val label: String, val replaceCost: Int) {
    CARBON_FIBER_SPLITTER("Carbon Fiber Splitter", 1250),
    POLYURETHANE_LIP("Polyurethane (PU) Lip", 320),
    ABS_PLASTIC_LIP("ABS Composite Lip", 240),
    OEM_PAINTED_BUMPER("OEM Painted Bumper", 850),
    NO_LIP("Bare Factory Bumper", 450)
}

enum class WheelProfile(val label: String, val rimRisk: String, val rimRepairCost: Int) {
    LOW_PROFILE_30_SERIES("30/25-Series Rubber Band", "CRITICAL RIM BEND RISK", 750),
    MID_PROFILE_35_SERIES("35-Series Sport Profile", "HIGH RIM BEND RISK", 550),
    STANDARD_40_SERIES("40-Series Tuner Profile", "MODERATE BUMP RISK", 350),
    MEATY_45_PLUS("45+ Meaty Track Tire", "LOW DAMAGE RISK", 200)
}

data class VehicleProfile(
    val id: String = "veh_1",
    val vehicleName: String = "BMW M3 Competition (Lowered)",
    val baseClearanceInches: Float = 2.8f,
    val suspensionType: SuspensionType = SuspensionType.AIR_SUSPENSION,
    val isLiftActive: Boolean = false,
    val liftHeightInches: Float = 1.8f,
    val frontLipType: FrontLipType = FrontLipType.CARBON_FIBER_SPLITTER,
    val wheelProfile: WheelProfile = WheelProfile.LOW_PROFILE_30_SERIES,
    val totalScrapesPrevented: Int = 18,
    val totalRepairMoneySaved: Int = 4650
) {
    val effectiveClearanceInches: Float
        get() = if (isLiftActive && suspensionType.canLift) {
            baseClearanceInches + liftHeightInches
        } else {
            baseClearanceInches
        }
}
