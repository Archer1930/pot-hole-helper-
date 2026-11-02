package com.example.data.model

enum class CameraSourceType(val label: String, val idealPosition: String) {
    INTEGRATED_OEM_DASHCAM("Integrated OEM Vision", "Windshield Mirror Console"),
    FRONT_BUMPER_LIP("Front Splitter Cam", "Lower Grille / Front Lip Edge"),
    UNDERBODY_LASER_CAM("Underbody Clearance Optical", "Chassis Subframe Skid Plate"),
    AUXILIARY_USB_DASHCAM("Added 4K Dashcam", "Upper Windshield Mount")
}

data class ConnectedCamera(
    val id: String,
    val name: String,
    val sourceType: CameraSourceType,
    val resolutionFps: String = "4K @ 60 FPS HDR",
    val isScanningActive: Boolean = true,
    val detectedDeformitiesCount: Int = 14
)

data class AiRoadDeformityDetection(
    val id: String,
    val label: String,
    val deformityType: HazardType,
    val estimatedDepthInches: Float,
    val confidencePercent: Int,
    val distanceFeet: Int,
    val lane: LanePosition,
    val boundingBoxTop: Float, // 0.0 - 1.0 normalized
    val boundingBoxLeft: Float,
    val boundingBoxWidth: Float,
    val boundingBoxHeight: Float,
    val timestamp: String = "11:34:56 AM",
    val autoLogged: Boolean = true
)
