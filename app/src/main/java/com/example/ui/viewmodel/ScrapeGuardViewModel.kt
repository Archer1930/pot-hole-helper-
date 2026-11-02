package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CameraSourceType
import com.example.data.model.ConnectedCamera
import com.example.data.model.FrontLipType
import com.example.data.model.HazardSeverity
import com.example.data.model.HazardType
import com.example.data.model.HistoricalHotspot
import com.example.data.model.LanePosition
import com.example.data.model.MunicipalDepartment
import com.example.data.model.MunicipalReportTicket
import com.example.data.model.Obd2ConnectionState
import com.example.data.model.Obd2Telemetry
import com.example.data.model.PotholeHazard
import com.example.data.model.RouteComparison
import com.example.data.model.SuspensionType
import com.example.data.model.VehicleProfile
import com.example.data.model.WeatherCondition
import com.example.data.model.WheelProfile
import com.example.data.repository.HazardRepository
import com.example.data.repository.VehicleRepository
import com.example.service.DriveNavigationManager
import com.example.service.DrivingMode
import com.example.service.RoadVibrationDetector
import com.example.ui.theme.AppThemeAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val label: String, val iconName: String) {
    RADAR_HUD("Live Radar", "radar"),
    ROUTE_REROUTE("Safe Detour", "alt_route"),
    DOT_DASHCAM("DOT 311 & Dashcam", "videocam"),
    HISTORICAL("Historical Intel", "analytics"),
    GARAGE("Clearance & Lip", "tune")
}

data class UiState(
    val currentTab: AppTab = AppTab.RADAR_HUD,
    val selectedHazard: PotholeHazard? = null,
    val isReportDialogOpen: Boolean = false,
    val isClearanceSliderExpanded: Boolean = false,
    val isWeatherPickerOpen: Boolean = false,
    val showScrapeNotification: Boolean = false,
    val dashcamViewingHazard: PotholeHazard? = null,
    val dotReportingHazard: PotholeHazard? = null,
    val isGpsHardwareModalOpen: Boolean = false,
    val isThemePickerOpen: Boolean = false,
    val isObd2ModalOpen: Boolean = false,
    val isCameraVisionModalOpen: Boolean = false
)

class ScrapeGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val hazardRepo = HazardRepository()
    private val vehicleRepo = VehicleRepository()
    val vibrationDetector = RoadVibrationDetector(application)
    val navigationManager = DriveNavigationManager(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _themeAccent = MutableStateFlow(AppThemeAccent.CYBER_CYAN)
    val themeAccent: StateFlow<AppThemeAccent> = _themeAccent.asStateFlow()

    private val _obd2Telemetry = MutableStateFlow(Obd2Telemetry())
    val obd2Telemetry: StateFlow<Obd2Telemetry> = _obd2Telemetry.asStateFlow()

    private val _connectedCameras = MutableStateFlow(
        listOf(
            ConnectedCamera(
                id = "cam_oem_1",
                name = "Integrated Windshield Vision",
                sourceType = CameraSourceType.INTEGRATED_OEM_DASHCAM,
                resolutionFps = "4K @ 60 FPS HDR",
                isScanningActive = true,
                detectedDeformitiesCount = 14
            ),
            ConnectedCamera(
                id = "cam_lip_2",
                name = "Front Splitter Optical Eye",
                sourceType = CameraSourceType.FRONT_BUMPER_LIP,
                resolutionFps = "1080P @ 120 FPS",
                isScanningActive = true,
                detectedDeformitiesCount = 9
            ),
            ConnectedCamera(
                id = "cam_skid_3",
                name = "Underbody Laser Cam",
                sourceType = CameraSourceType.UNDERBODY_LASER_CAM,
                resolutionFps = "Active LiDAR Optical",
                isScanningActive = false,
                detectedDeformitiesCount = 3
            )
        )
    )
    val connectedCameras: StateFlow<List<ConnectedCamera>> = _connectedCameras.asStateFlow()

    val hazards: StateFlow<List<PotholeHazard>> = hazardRepo.hazards
    val municipalTickets: StateFlow<List<MunicipalReportTicket>> = hazardRepo.municipalTickets
    val historicalHotspots: StateFlow<List<HistoricalHotspot>> = hazardRepo.historicalHotspots
    val currentWeather: StateFlow<WeatherCondition> = hazardRepo.currentWeather
    val vehicleProfile: StateFlow<VehicleProfile> = vehicleRepo.currentProfile
    val vehiclePresets = vehicleRepo.vehiclePresets

    val telemetry = navigationManager.telemetry
    val vibration = vibrationDetector.vibrationState

    val routeComparison: StateFlow<RouteComparison> = combine(
        vehicleProfile,
        currentWeather
    ) { vehicle, _ ->
        hazardRepo.getRouteComparison(vehicle.effectiveClearanceInches)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        hazardRepo.getRouteComparison(2.8f)
    )

    init {
        vibrationDetector.startListening()
    }

    override fun onCleared() {
        super.onCleared()
        vibrationDetector.stopListening()
        navigationManager.stopDrive()
    }

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectHazard(hazard: PotholeHazard?) {
        _uiState.value = _uiState.value.copy(selectedHazard = hazard)
    }

    fun openReportDialog(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isReportDialogOpen = isOpen)
    }

    fun openWeatherPicker(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isWeatherPickerOpen = isOpen)
    }

    fun openDashcamViewer(hazard: PotholeHazard?) {
        _uiState.value = _uiState.value.copy(dashcamViewingHazard = hazard)
    }

    fun openDotReportDialog(hazard: PotholeHazard?) {
        _uiState.value = _uiState.value.copy(dotReportingHazard = hazard)
    }

    fun openGpsHardwareModal(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isGpsHardwareModalOpen = isOpen)
    }

    fun setWeather(condition: WeatherCondition) {
        hazardRepo.updateWeather(condition)
        _uiState.value = _uiState.value.copy(isWeatherPickerOpen = false)
    }

    fun toggleAirLift() {
        vehicleRepo.toggleAirLift()
    }

    fun updateClearance(inches: Float) {
        vehicleRepo.updateClearance(inches)
    }

    fun updateVehicleName(name: String) {
        vehicleRepo.updateVehicleName(name)
    }

    fun updateSuspension(type: SuspensionType) {
        vehicleRepo.updateSuspension(type)
    }

    fun updateFrontLip(lip: FrontLipType) {
        vehicleRepo.updateFrontLip(lip)
    }

    fun updateWheel(wheel: WheelProfile) {
        vehicleRepo.updateWheelProfile(wheel)
    }

    fun loadPreset(preset: VehicleProfile) {
        vehicleRepo.loadPreset(preset)
    }

    fun startDriveSimulation() {
        navigationManager.startDriveSimulation(hazards.value, vehicleProfile.value)
    }

    fun stopDriveSimulation() {
        navigationManager.stopDrive()
    }

    fun toggleFloatingHud() {
        navigationManager.toggleFloatingHud()
    }

    fun testBumpShock() {
        vibrationDetector.simulateBumpSpike()
    }

    fun launchGoogleMapsSafeRoute() {
        val route = routeComparison.value.scrapeGuardDetourRoute
        navigationManager.launchGoogleMapsNavigation(route.waypointsQuery)
    }

    fun submitPotholeReport(
        street: String,
        crossStreet: String,
        depthInches: Float,
        severity: HazardSeverity,
        type: HazardType,
        lane: LanePosition,
        notes: String,
        photoDrawable: Int?,
        photoUri: String?,
        department: MunicipalDepartment,
        fileMunicipalTicket: Boolean
    ) {
        val createdHazard = hazardRepo.reportHazard(
            street = street,
            crossStreet = crossStreet,
            depthInches = depthInches,
            severity = severity,
            type = type,
            lane = lane,
            notes = notes,
            photoDrawable = photoDrawable,
            photoUri = photoUri,
            department = department
        )

        if (fileMunicipalTicket) {
            hazardRepo.submitMunicipalTicket(
                hazard = createdHazard,
                department = department,
                driverStatement = notes,
                photoDrawable = photoDrawable,
                photoUri = photoUri
            )
        }

        _uiState.value = _uiState.value.copy(isReportDialogOpen = false)
    }

    fun submitMunicipalTicket(
        hazard: PotholeHazard,
        department: MunicipalDepartment,
        driverStatement: String
    ) {
        hazardRepo.submitMunicipalTicket(
            hazard = hazard,
            department = department,
            driverStatement = driverStatement,
            photoDrawable = hazard.dashcamPhotoDrawable,
            photoUri = hazard.customPhotoUri
        )
    }

    fun setThemeAccent(accent: AppThemeAccent) {
        _themeAccent.value = accent
    }

    fun openThemePicker(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isThemePickerOpen = isOpen)
    }

    fun openObd2Modal(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isObd2ModalOpen = isOpen)
    }

    fun openCameraVisionModal(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isCameraVisionModalOpen = isOpen)
    }

    fun setWarningThreshold(feet: Int) {
        navigationManager.setWarningThreshold(feet)
    }

    fun toggleObd2Connection() {
        val current = _obd2Telemetry.value
        val nextState = when (current.connectionState) {
            Obd2ConnectionState.CONNECTED -> Obd2ConnectionState.DISCONNECTED
            Obd2ConnectionState.DISCONNECTED -> Obd2ConnectionState.CONNECTING
            Obd2ConnectionState.CONNECTING -> Obd2ConnectionState.CONNECTED
        }
        _obd2Telemetry.value = current.copy(connectionState = nextState)
    }

    fun simulateObd2WheelDrop() {
        viewModelScope.launch {
            _obd2Telemetry.value = _obd2Telemetry.value.copy(
                wheelDropSpikeDetected = true,
                suspensionClearanceMm = 35 // Compressed/plunged into pothole
            )
            vibrationDetector.simulateBumpSpike()
            delay(1800)
            _obd2Telemetry.value = _obd2Telemetry.value.copy(
                wheelDropSpikeDetected = false,
                suspensionClearanceMm = 71 // Restored to 2.8"
            )
        }
    }

    fun addCamera(name: String, type: CameraSourceType) {
        val newCam = ConnectedCamera(
            id = "cam_${System.currentTimeMillis()}",
            name = name,
            sourceType = type,
            resolutionFps = "4K @ 60 FPS HDR",
            isScanningActive = true,
            detectedDeformitiesCount = 1
        )
        _connectedCameras.value = _connectedCameras.value + newCam
    }

    fun togglePotholeWayMode() {
        navigationManager.togglePotholeWayMode()
    }

    fun selectLane(lane: LanePosition) {
        navigationManager.selectLane(lane)
    }

    fun logCameraDetectionToRadar(title: String, depth: Float) {
        hazardRepo.reportHazard(
            street = title,
            crossStreet = "Optical Cam Logged",
            depthInches = depth,
            severity = if (depth >= 3.0f) HazardSeverity.CATASTROPHIC else HazardSeverity.MODERATE,
            type = if (title.contains("Sewer", ignoreCase = true)) HazardType.RAISED_MANHOLE else HazardType.DEEP_POTHOLE,
            lane = LanePosition.CENTER_LANE,
            notes = "AI Neural Vision verified road deformity from active dashcam feed. Advance warning 2,640 ft broadcasted to ScrapeGuard network.",
            photoDrawable = com.example.R.drawable.ai_vision_road_scanner,
            photoUri = null,
            department = MunicipalDepartment.CITY_DOT_311
        )
    }
}
