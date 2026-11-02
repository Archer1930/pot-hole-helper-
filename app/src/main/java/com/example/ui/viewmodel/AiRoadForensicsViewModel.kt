package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.HistoricalCluster
import com.example.data.model.PotholeHazard
import com.example.data.model.VehicleProfile
import com.example.data.model.WeatherCondition
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "AiRoadForensicsVM"

enum class AiForensicsMode(val title: String) {
    HAZARD_DIAGNOSIS("Hazard Damage Forensics"),
    DOT_311_CLAIM("DOT 311 Legal Notice"),
    FREEZE_THAW_EXPLANATION("Freeze-Thaw Climate Risk"),
    LIVE_TACTICAL_CHAT("Lowered Car Road Copilot")
}

sealed interface AiForensicsUiState {
    data object Idle : AiForensicsUiState
    data class Loading(val mode: AiForensicsMode) : AiForensicsUiState
    data class Streaming(val text: String, val mode: AiForensicsMode) : AiForensicsUiState
    data class Success(
        val text: String,
        val mode: AiForensicsMode,
        val generatedAtMillis: Long = System.currentTimeMillis()
    ) : AiForensicsUiState
    data class Error(val message: String, val mode: AiForensicsMode) : AiForensicsUiState
}

class AiRoadForensicsViewModel : ViewModel() {

    // Lazy initialization as mandated by Firebase AI Logic skill:
    // evaluated on first inference rather than eagerly at class construction time.
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    private val _uiState = MutableStateFlow<AiForensicsUiState>(AiForensicsUiState.Idle)
    val uiState: StateFlow<AiForensicsUiState> = _uiState.asStateFlow()

    private val _lastGeneratedClaim = MutableStateFlow<String?>(null)
    val lastGeneratedClaim: StateFlow<String?> = _lastGeneratedClaim.asStateFlow()

    fun resetState() {
        _uiState.value = AiForensicsUiState.Idle
    }

    /**
     * AI Road Hazard & Damage Forensics:
     * Analyzes pothole geometry vs vehicle clearance and stance to calculate
     * structural damage probabilities (splitter scrape, oil pan crack, rim bend).
     */
    fun analyzeHazard(hazard: PotholeHazard, vehicle: VehicleProfile) {
        viewModelScope.launch {
            _uiState.value = AiForensicsUiState.Loading(AiForensicsMode.HAZARD_DIAGNOSIS)
            val prompt = buildString {
                appendLine("You are ScrapeGuard's AI Road Surface & Vehicle Forensics Engineer.")
                appendLine("Analyze the following road hazard strike risk for a lowered/stanced vehicle:")
                appendLine("- Hazard Type: ${hazard.type.name}")
                appendLine("- Street / Location: ${hazard.streetName} (${hazard.latitude}, ${hazard.longitude})")
                appendLine("- Severity Rating: ${hazard.severity.name}")
                appendLine("- Measured Depth: ${hazard.depthInches} inches")
                appendLine("- Hazard Width: ${hazard.widthInches} inches")
                appendLine("- Hazard Lane: ${hazard.lanePosition.name}")
                appendLine("- Vehicle Name: ${vehicle.name}")
                appendLine("- Ground Clearance: ${vehicle.groundClearanceInches} inches")
                appendLine("- Stance Style: ${vehicle.stanceStyle.name}")
                appendLine("- Front Splitter Installed: ${vehicle.hasFrontLip}")
                appendLine("- Low Profile Tires: ${vehicle.hasLowProfileTires}")
                appendLine("- Stiff Track Coilovers: ${vehicle.suspensionStiffness.name}")
                appendLine()
                appendLine("Provide a crisp, professional diagnostic summary with:")
                appendLine("1. CLEARANCE DELTA: Exact calculation of margin (vehicle clearance vs hazard depth).")
                appendLine("2. DAMAGE RISK AUDIT: Specific risks to front splitter/lip, forged/alloy rim edge, oil pan, and subframe.")
                appendLine("3. EVASION & STRATAGEM: Best lane positioning or approach maneuver (angle-of-attack if unavoidable).")
                appendLine("4. ASPHALT GEOTECHNICAL ROOT CAUSE: Why this deformation occurred (water intrusion, freeze-thaw spall, heavy axle load).")
            }

            streamPrompt(prompt, AiForensicsMode.HAZARD_DIAGNOSIS)
        }
    }

    /**
     * Automated Official DOT / 311 Municipal Claim Notice:
     * Drafts formal tort liability notice pre-filled with legal roadway maintenance duty citations,
     * GPS proof, repair urgency tier, and vehicle repair claim.
     */
    fun generate311ClaimNotice(
        hazard: PotholeHazard,
        vehicle: VehicleProfile,
        incidentDate: String = "Today",
        customNotes: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = AiForensicsUiState.Loading(AiForensicsMode.DOT_311_CLAIM)
            val prompt = buildString {
                appendLine("You are an expert municipal public works & tort claims legal drafter.")
                appendLine("Generate an official, formal 311 Road Hazard Work Order & Municipal Notice of Claim.")
                appendLine("- Defect Location: ${hazard.streetName}")
                appendLine("- Coordinates: GPS Lat ${hazard.latitude}, Lng ${hazard.longitude}")
                appendLine("- Defect Dimensions: ${hazard.depthInches}\" depth x ${hazard.widthInches}\" width (${hazard.type.name})")
                appendLine("- Affected Lane: ${hazard.lanePosition.name}")
                appendLine("- Reporting Vehicle: ${vehicle.name} (Clearance: ${vehicle.groundClearanceInches}\")")
                appendLine("- Date of Incident / Detection: $incidentDate")
                if (customNotes.isNotBlank()) {
                    appendLine("- Driver Remarks: $customNotes")
                }
                appendLine()
                appendLine("Format the output as a clean, ready-to-file administrative notice:")
                appendLine("1. FORMAL NOTICE HEADER: To City Department of Transportation / Bureau of Street Services.")
                appendLine("2. DEFECT SPECIFICATION: Accurate engineering description of road failure.")
                appendLine("3. STATUTORY NOTICE OF DANGEROUS CONDITION: Standard municipal road maintenance liability citation (citing duty to maintain public streets in safe vehicular condition).")
                appendLine("4. REMEDIAL ACTION REQUIRED: Cold-patch urgency rating and asphalt mill-and-fill recommendation.")
                appendLine("5. VEHICULAR DAMAGE & EXPENSE RESERVATION: Reservation of rights for rim repair, alignment, or undercarriage inspection.")
            }

            streamPrompt(prompt, AiForensicsMode.DOT_311_CLAIM) { fullText ->
                _lastGeneratedClaim.value = fullText
            }
        }
    }

    /**
     * Freeze-Thaw Climate Forensics:
     * Explains why temperature fluctuations around 32°F / 0°C create explosive asphalt blowouts
     * and forecasts pavement degradation for the user's specific region.
     */
    fun analyzeFreezeThawCycle(
        weather: WeatherCondition,
        cluster: HistoricalCluster?,
        groundTempF: Int = 30
    ) {
        viewModelScope.launch {
            _uiState.value = AiForensicsUiState.Loading(AiForensicsMode.FREEZE_THAW_EXPLANATION)
            val prompt = buildString {
                appendLine("You are ScrapeGuard's asphalt pavement materials scientist.")
                appendLine("Explain the geotechnical freeze-thaw risk to road drivers based on current climate conditions:")
                appendLine("- Current Air Temp: ${weather.temperatureF}°F (${weather.name})")
                appendLine("- Ground Subsurface Temp: ${groundTempF}°F")
                appendLine("- Freeze-Thaw Cycle Active: ${weather.isFreezeThawActive}")
                appendLine("- Pothole Risk Level: ${weather.potholeRiskLevel.name}")
                if (cluster != null) {
                    appendLine("- Target Area: ${cluster.name} (${cluster.reportCount} historical failures, risk score ${cluster.riskScore}/100)")
                }
                appendLine()
                appendLine("Provide:")
                appendLine("1. WATER-ICE EXPANSION PHYSICS: How sub-base moisture expansion (9% volume increase) breaks aggregate bond.")
                appendLine("2. 48-HOUR ASPHALT INTEGRITY FORECAST: What lowered cars should expect on arterial and collector streets.")
                appendLine("3. COLD-WEATHER SUSPENSION VULNERABILITY: How frozen coilover damping oil and stiff cold tires exacerbate chassis impact.")
            }

            streamPrompt(prompt, AiForensicsMode.FREEZE_THAW_EXPLANATION)
        }
    }

    /**
     * Interactive Tactical Chat for Lowered Car Guidance:
     */
    fun askTacticalCopilot(userQuestion: String, currentContext: String) {
        viewModelScope.launch {
            _uiState.value = AiForensicsUiState.Loading(AiForensicsMode.LIVE_TACTICAL_CHAT)
            val prompt = buildString {
                appendLine("You are ScrapeGuard Road Copilot, specialized in helping lowered, stanced, and sports car drivers navigate rough city roads safely.")
                appendLine("Current Driving Telemetry & Context:")
                appendLine(currentContext)
                appendLine()
                appendLine("Driver's question: $userQuestion")
                appendLine()
                appendLine("Answer with concise, actionable, automotive-savvy advice (under 120 words).")
            }

            streamPrompt(prompt, AiForensicsMode.LIVE_TACTICAL_CHAT)
        }
    }

    private suspend fun streamPrompt(
        prompt: String,
        mode: AiForensicsMode,
        onComplete: ((String) -> Unit)? = null
    ) {
        val accumulated = StringBuilder()
        try {
            // Mandated: realistic 30-60s timeout for model inference
            withTimeout(60_000L) {
                model.generateContentStream(prompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    accumulated.append(chunkText)
                    _uiState.value = AiForensicsUiState.Streaming(accumulated.toString(), mode)
                }
            }

            val finalOutput = accumulated.toString()
            if (finalOutput.isNotBlank()) {
                _uiState.value = AiForensicsUiState.Success(finalOutput, mode)
                onComplete?.invoke(finalOutput)
            } else {
                _uiState.value = AiForensicsUiState.Error("Gemini returned an empty response.", mode)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini generation timed out", e)
            _uiState.value = AiForensicsUiState.Error(
                "Generation timed out after 60 seconds. Please try again.",
                mode
            )
        } catch (e: CancellationException) {
            // Mandated: ALWAYS re-throw CancellationException to preserve cooperative cancellation
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation failed", e)
            // Mandated: Transparent error propagation to reveal backend status
            _uiState.value = AiForensicsUiState.Error(
                e.localizedMessage ?: "AI generation failed.",
                mode
            )
        }
    }
}
