package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.SignInScreen
import com.example.auth.authStateFlow
import com.example.ui.components.CameraVisionScannerModal
import com.example.ui.components.DashcamViewerDialog
import com.example.ui.components.GpsHardwareModal
import com.example.ui.components.MunicipalReportDialog
import com.example.ui.components.Obd2AdapterModal
import com.example.ui.components.ReportPotholeDialog
import com.example.ui.components.ThemeCustomizerDialog
import com.example.ui.components.WeatherPickerDialog
import com.example.ui.screens.DotWorkOrdersScreen
import com.example.ui.screens.HistoricalAnalyticsScreen
import com.example.ui.screens.NavigationHudScreen
import com.example.ui.screens.RouteCompareScreen
import com.example.ui.screens.VehicleGarageScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkAsphalt
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.ScrapeGuardViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ScrapeGuardViewModel = viewModel()
            val themeAccent by viewModel.themeAccent.collectAsState()
            val currentUser by Firebase.auth.authStateFlow().collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)
            var isGuestMode by remember { mutableStateOf(false) }

            MyApplicationTheme(accent = themeAccent) {
                if (currentUser == null && !isGuestMode) {
                    SignInScreen(
                        onAuthSuccess = { /* Automatically navigates as currentUser becomes non-null */ },
                        onSkipToLocalMode = { isGuestMode = true }
                    )
                } else {
                    ScrapeGuardApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ScrapeGuardApp(
    viewModel: ScrapeGuardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hazards by viewModel.hazards.collectAsState()
    val municipalTickets by viewModel.municipalTickets.collectAsState()
    val historicalHotspots by viewModel.historicalHotspots.collectAsState()
    val currentWeather by viewModel.currentWeather.collectAsState()
    val vehicleProfile by viewModel.vehicleProfile.collectAsState()
    val routeComparison by viewModel.routeComparison.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val vibration by viewModel.vibration.collectAsState()
    val obd2Telemetry by viewModel.obd2Telemetry.collectAsState()
    val connectedCameras by viewModel.connectedCameras.collectAsState()
    val themeAccent by viewModel.themeAccent.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkAsphalt),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar"),
                containerColor = SurfaceDark,
                contentColor = TextSecondary
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    val icon = when (tab) {
                        AppTab.RADAR_HUD -> Icons.Default.Radar
                        AppTab.ROUTE_REROUTE -> Icons.Default.AltRoute
                        AppTab.DOT_DASHCAM -> Icons.Default.Videocam
                        AppTab.HISTORICAL -> Icons.Default.Analytics
                        AppTab.GARAGE -> Icons.Default.Tune
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = when (tab) {
                                    AppTab.RADAR_HUD -> "Radar"
                                    AppTab.ROUTE_REROUTE -> "Routes"
                                    AppTab.DOT_DASHCAM -> "311 & Cam"
                                    AppTab.HISTORICAL -> "History"
                                    AppTab.GARAGE -> "Clearance"
                                },
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkAsphalt,
                            selectedTextColor = themeAccent.primaryColor,
                            indicatorColor = themeAccent.primaryColor,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (uiState.currentTab) {
                AppTab.RADAR_HUD -> {
                    NavigationHudScreen(
                        hazards = hazards,
                        selectedHazard = uiState.selectedHazard,
                        vehicle = vehicleProfile,
                        weather = currentWeather,
                        telemetry = telemetry,
                        vibration = vibration,
                        obd2Telemetry = obd2Telemetry,
                        onHazardSelected = { viewModel.selectHazard(it) },
                        onToggleAirLift = { viewModel.toggleAirLift() },
                        onStartSimulation = { viewModel.startDriveSimulation() },
                        onStopSimulation = { viewModel.stopDriveSimulation() },
                        onOpenWeatherDialog = { viewModel.openWeatherPicker(true) },
                        onOpenReportDialog = { viewModel.openReportDialog(true) },
                        onNavigateSafeDetour = { viewModel.selectTab(AppTab.ROUTE_REROUTE) },
                        onLaunchGoogleMaps = { viewModel.launchGoogleMapsSafeRoute() },
                        onTestBump = { viewModel.testBumpShock() },
                        onViewDashcam = { viewModel.openDashcamViewer(it) },
                        onReportToDot = { viewModel.openDotReportDialog(it) },
                        onOpenGpsHardwareModal = { viewModel.openGpsHardwareModal(true) },
                        onOpenObd2Modal = { viewModel.openObd2Modal(true) },
                        onOpenCameraVisionModal = { viewModel.openCameraVisionModal(true) },
                        onOpenThemePicker = { viewModel.openThemePicker(true) },
                        onSelectLane = { viewModel.selectLane(it) }
                    )
                }

                AppTab.ROUTE_REROUTE -> {
                    RouteCompareScreen(
                        routeComparison = routeComparison,
                        vehicle = vehicleProfile,
                        onLaunchGoogleMapsSafeRoute = { viewModel.launchGoogleMapsSafeRoute() },
                        onNavigatePotholeWay = {
                            viewModel.selectTab(AppTab.RADAR_HUD)
                            viewModel.startDriveSimulation()
                        }
                    )
                }

                AppTab.DOT_DASHCAM -> {
                    DotWorkOrdersScreen(
                        tickets = municipalTickets,
                        hazardsWithPhotos = hazards.filter { it.dashcamPhotoDrawable != null || !it.customPhotoUri.isNullOrBlank() },
                        onViewHazardDashcam = { viewModel.openDashcamViewer(it) },
                        onOpenReportDialog = { viewModel.openReportDialog(true) }
                    )
                }

                AppTab.HISTORICAL -> {
                    HistoricalAnalyticsScreen(
                        historicalHotspots = historicalHotspots,
                        currentWeather = currentWeather,
                        onSelectWeather = { viewModel.setWeather(it) }
                    )
                }

                AppTab.GARAGE -> {
                    VehicleGarageScreen(
                        vehicle = vehicleProfile,
                        presets = viewModel.vehiclePresets,
                        onUpdateClearance = { viewModel.updateClearance(it) },
                        onUpdateName = { viewModel.updateVehicleName(it) },
                        onUpdateSuspension = { viewModel.updateSuspension(it) },
                        onUpdateLip = { viewModel.updateFrontLip(it) },
                        onUpdateWheel = { viewModel.updateWheel(it) },
                        onToggleAirLift = { viewModel.toggleAirLift() },
                        onLoadPreset = { viewModel.loadPreset(it) }
                    )
                }
            }

            // Report Pothole Modal
            if (uiState.isReportDialogOpen) {
                ReportPotholeDialog(
                    onDismiss = { viewModel.openReportDialog(false) },
                    onSubmit = { street, crossStreet, depth, severity, type, lane, notes, photoDrawable, photoUri, dept, fileTicket ->
                        viewModel.submitPotholeReport(
                            street,
                            crossStreet,
                            depth,
                            severity,
                            type,
                            lane,
                            notes,
                            photoDrawable,
                            photoUri,
                            dept,
                            fileTicket
                        )
                    }
                )
            }

            // Weather Simulator Modal
            if (uiState.isWeatherPickerOpen) {
                WeatherPickerDialog(
                    currentWeather = currentWeather,
                    onSelectWeather = { viewModel.setWeather(it) },
                    onDismiss = { viewModel.openWeatherPicker(false) }
                )
            }

            // Dashcam Photo Viewer Modal
            uiState.dashcamViewingHazard?.let { hazard ->
                DashcamViewerDialog(
                    hazard = hazard,
                    onDismiss = { viewModel.openDashcamViewer(null) },
                    onReportToDot = {
                        viewModel.openDashcamViewer(null)
                        viewModel.openDotReportDialog(it)
                    }
                )
            }

            // Municipal DOT Work Order Filing Modal
            uiState.dotReportingHazard?.let { hazard ->
                MunicipalReportDialog(
                    hazard = hazard,
                    onDismiss = { viewModel.openDotReportDialog(null) },
                    onSubmitTicket = { h, dept, stmt ->
                        viewModel.submitMunicipalTicket(h, dept, stmt)
                    }
                )
            }

            // ScrapeGuard ProLink Standalone GPS Hardware Modal
            if (uiState.isGpsHardwareModalOpen) {
                GpsHardwareModal(
                    onDismiss = { viewModel.openGpsHardwareModal(false) }
                )
            }

            // Theme Color Customizer Modal
            if (uiState.isThemePickerOpen) {
                ThemeCustomizerDialog(
                    currentAccent = themeAccent,
                    onSelectAccent = { viewModel.setThemeAccent(it) },
                    onDismiss = { viewModel.openThemePicker(false) }
                )
            }

            // OBD2 CAN-bus Adapter Modal
            if (uiState.isObd2ModalOpen) {
                Obd2AdapterModal(
                    obd2Telemetry = obd2Telemetry,
                    onSimulateWheelDrop = { viewModel.simulateObd2WheelDrop() },
                    onToggleConnection = { viewModel.toggleObd2Connection() },
                    onDismiss = { viewModel.openObd2Modal(false) }
                )
            }

            // AI Camera Vision Road Scanner Modal
            if (uiState.isCameraVisionModalOpen) {
                CameraVisionScannerModal(
                    connectedCameras = connectedCameras,
                    onAddCamera = { name, type -> viewModel.addCamera(name, type) },
                    onLogDetectionToRadar = { title, depth -> viewModel.logCameraDetectionToRadar(title, depth) },
                    onDismiss = { viewModel.openCameraVisionModal(false) }
                )
            }
        }
    }
}
