package com.example.data.repository

import com.example.data.model.FrontLipType
import com.example.data.model.SuspensionType
import com.example.data.model.VehicleProfile
import com.example.data.model.WheelProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleRepository {

    private val _currentProfile = MutableStateFlow(
        VehicleProfile(
            id = "user_vehicle",
            vehicleName = "BMW M3 Competition (Lowered)",
            baseClearanceInches = 2.8f,
            suspensionType = SuspensionType.AIR_SUSPENSION,
            isLiftActive = false,
            liftHeightInches = 1.8f,
            frontLipType = FrontLipType.CARBON_FIBER_SPLITTER,
            wheelProfile = WheelProfile.LOW_PROFILE_30_SERIES,
            totalScrapesPrevented = 18,
            totalRepairMoneySaved = 4650
        )
    )
    val currentProfile: StateFlow<VehicleProfile> = _currentProfile.asStateFlow()

    val vehiclePresets = listOf(
        VehicleProfile(
            id = "preset_slammed",
            vehicleName = "Stance / Slammed Fitment",
            baseClearanceInches = 2.0f,
            suspensionType = SuspensionType.STATIC_COILOVERS,
            isLiftActive = false,
            frontLipType = FrontLipType.CARBON_FIBER_SPLITTER,
            wheelProfile = WheelProfile.LOW_PROFILE_30_SERIES
        ),
        VehicleProfile(
            id = "preset_airlift",
            vehicleName = "Air Lift Performance Spec",
            baseClearanceInches = 2.6f,
            suspensionType = SuspensionType.AIR_SUSPENSION,
            isLiftActive = false,
            liftHeightInches = 1.8f,
            frontLipType = FrontLipType.CARBON_FIBER_SPLITTER,
            wheelProfile = WheelProfile.LOW_PROFILE_30_SERIES
        ),
        VehicleProfile(
            id = "preset_track",
            vehicleName = "Track Tuned / Coilovers",
            baseClearanceInches = 3.3f,
            suspensionType = SuspensionType.STATIC_COILOVERS,
            isLiftActive = false,
            frontLipType = FrontLipType.POLYURETHANE_LIP,
            wheelProfile = WheelProfile.MID_PROFILE_35_SERIES
        ),
        VehicleProfile(
            id = "preset_springs",
            vehicleName = "Sport Lowering Springs",
            baseClearanceInches = 4.0f,
            suspensionType = SuspensionType.LOWERING_SPRINGS,
            isLiftActive = false,
            frontLipType = FrontLipType.ABS_PLASTIC_LIP,
            wheelProfile = WheelProfile.STANDARD_40_SERIES
        )
    )

    fun toggleAirLift() {
        val current = _currentProfile.value
        if (current.suspensionType.canLift) {
            _currentProfile.value = current.copy(isLiftActive = !current.isLiftActive)
        }
    }

    fun updateClearance(newClearance: Float) {
        _currentProfile.value = _currentProfile.value.copy(
            baseClearanceInches = (newClearance * 10).toInt() / 10f
        )
    }

    fun updateVehicleName(name: String) {
        _currentProfile.value = _currentProfile.value.copy(vehicleName = name)
    }

    fun updateSuspension(type: SuspensionType) {
        _currentProfile.value = _currentProfile.value.copy(
            suspensionType = type,
            isLiftActive = if (!type.canLift) false else _currentProfile.value.isLiftActive
        )
    }

    fun updateFrontLip(lipType: FrontLipType) {
        _currentProfile.value = _currentProfile.value.copy(frontLipType = lipType)
    }

    fun updateWheelProfile(wheelProfile: WheelProfile) {
        _currentProfile.value = _currentProfile.value.copy(wheelProfile = wheelProfile)
    }

    fun loadPreset(preset: VehicleProfile) {
        _currentProfile.value = preset.copy(
            totalScrapesPrevented = _currentProfile.value.totalScrapesPrevented,
            totalRepairMoneySaved = _currentProfile.value.totalRepairMoneySaved
        )
    }

    fun recordScrapeAvoided(savedValueDollars: Int) {
        val current = _currentProfile.value
        _currentProfile.value = current.copy(
            totalScrapesPrevented = current.totalScrapesPrevented + 1,
            totalRepairMoneySaved = current.totalRepairMoneySaved + savedValueDollars
        )
    }
}
