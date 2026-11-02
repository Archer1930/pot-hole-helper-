package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HistoricalCluster
import com.example.data.model.PotholeHazard
import com.example.data.model.VehicleProfile
import com.example.data.model.WeatherCondition
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkAsphalt
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.RoadAlertOrange
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiForensicsMode
import com.example.ui.viewmodel.AiForensicsUiState
import com.example.ui.viewmodel.AiRoadForensicsViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiRoadForensicsModal(
    hazard: PotholeHazard,
    vehicleProfile: VehicleProfile,
    currentWeather: WeatherCondition,
    historicalHotspots: List<HistoricalCluster>,
    viewModel: AiRoadForensicsViewModel,
    onDismiss: () -> Unit,
    onApplyClaimToReport: ((String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(AiForensicsMode.HAZARD_DIAGNOSIS) }
    var userCustomQuestion by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Automatically trigger analysis on modal open if idle
    LaunchedEffect(hazard.id, selectedMode) {
        if (uiState is AiForensicsUiState.Idle) {
            when (selectedMode) {
                AiForensicsMode.HAZARD_DIAGNOSIS -> viewModel.analyzeHazard(hazard, vehicleProfile)
                AiForensicsMode.DOT_311_CLAIM -> viewModel.generate311ClaimNotice(hazard, vehicleProfile)
                AiForensicsMode.FREEZE_THAW_EXPLANATION -> viewModel.analyzeFreezeThawCycle(currentWeather, historicalHotspots.firstOrNull())
                AiForensicsMode.LIVE_TACTICAL_CHAT -> { /* Wait for user prompt */ }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("ai_forensics_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = DarkAsphalt,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyberCyan.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Model",
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ScrapeGuard AI Forensics",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Gemini 3.8 Flash",
                                        color = ElectricBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                text = "App Check Protected • Real-Time Road Diagnostics",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_ai_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AiForensicsMode.values().forEach { mode ->
                        val isSelected = selectedMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedMode = mode
                                when (mode) {
                                    AiForensicsMode.HAZARD_DIAGNOSIS -> viewModel.analyzeHazard(hazard, vehicleProfile)
                                    AiForensicsMode.DOT_311_CLAIM -> viewModel.generate311ClaimNotice(hazard, vehicleProfile)
                                    AiForensicsMode.FREEZE_THAW_EXPLANATION -> viewModel.analyzeFreezeThawCycle(currentWeather, historicalHotspots.firstOrNull())
                                    AiForensicsMode.LIVE_TACTICAL_CHAT -> {
                                        if (userCustomQuestion.isNotBlank()) {
                                            viewModel.askTacticalCopilot(userCustomQuestion, "Hazard: ${hazard.streetName}, Depth: ${hazard.depthInches}\", Clearance: ${vehicleProfile.groundClearanceInches}\"")
                                        }
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = when (mode) {
                                        AiForensicsMode.HAZARD_DIAGNOSIS -> "Damage Audit"
                                        AiForensicsMode.DOT_311_CLAIM -> "311 Legal Notice"
                                        AiForensicsMode.FREEZE_THAW_EXPLANATION -> "Freeze-Thaw"
                                        AiForensicsMode.LIVE_TACTICAL_CHAT -> "Ask Copilot"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                val icon = when (mode) {
                                    AiForensicsMode.HAZARD_DIAGNOSIS -> Icons.Default.Warning
                                    AiForensicsMode.DOT_311_CLAIM -> Icons.Default.Gavel
                                    AiForensicsMode.FREEZE_THAW_EXPLANATION -> Icons.Default.Thermostat
                                    AiForensicsMode.LIVE_TACTICAL_CHAT -> Icons.Default.Psychology
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) CyberCyan else TextMuted
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceDark,
                                labelColor = TextSecondary,
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyberCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) CyberCyan else Color.Transparent
                            ),
                            modifier = Modifier.testTag("ai_mode_chip_${mode.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Telemetry Context Summary Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = hazard.streetName,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Depth: ${hazard.depthInches}\" • Width: ${hazard.widthInches}\" • ${hazard.lanePosition.name}",
                            color = RoadAlertOrange,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = vehicleProfile.name,
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        val clearanceMargin = vehicleProfile.groundClearanceInches - hazard.depthInches
                        Text(
                            text = "Clearance: ${vehicleProfile.groundClearanceInches}\" (Delta: ${if (clearanceMargin >= 0) "+" else ""}${String.format("%.1f", clearanceMargin)}\")",
                            color = if (clearanceMargin < 0.5f) DangerRed else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (clearanceMargin < 0.5f) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Question Input Bar for LIVE_TACTICAL_CHAT
                if (selectedMode == AiForensicsMode.LIVE_TACTICAL_CHAT) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userCustomQuestion,
                            onValueChange = { userCustomQuestion = it },
                            placeholder = { Text("Ask road strategy (e.g. angle of attack)...", fontSize = 12.sp, color = TextMuted) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_copilot_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = SurfaceGlass,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (userCustomQuestion.isNotBlank()) {
                                    viewModel.askTacticalCopilot(
                                        userQuestion = userCustomQuestion,
                                        currentContext = "Target: ${hazard.streetName}, Depth: ${hazard.depthInches}\", Clearance: ${vehicleProfile.groundClearanceInches}\", Stance: ${vehicleProfile.stanceStyle}"
                                    )
                                }
                            },
                            enabled = userCustomQuestion.isNotBlank() && uiState !is AiForensicsUiState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ai_copilot_send_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send Prompt", tint = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggested quick chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "What angle should I enter?",
                            "Will this crack my oil pan?",
                            "Best lane to bypass?"
                        ).forEach { quickPrompt ->
                            Box(
                                modifier = Modifier
                                    .background(SurfaceDark, RoundedCornerShape(8.dp))
                                    .border(1.dp, SurfaceGlass, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = quickPrompt,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.testTag("quick_prompt_${quickPrompt.take(10)}")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // AI Output Display Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(SurfaceDark, RoundedCornerShape(12.dp))
                        .border(1.dp, SurfaceGlass, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    when (val state = uiState) {
                        is AiForensicsUiState.Idle -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterVertically,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ready to analyze road physics and generate legal filings.",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.analyzeHazard(hazard, vehicleProfile)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("trigger_forensics_btn")
                                ) {
                                    Text("Run Forensics Audit", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        is AiForensicsUiState.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterVertically,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = CyberCyan,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .testTag("ai_loading_spinner")
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Querying Gemini 3.8 Flash...",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Evaluating pavement fracture kinematics & clearance delta",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        is AiForensicsUiState.Streaming -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(CyberCyan, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "STREAMING IN REAL-TIME",
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = state.text,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    modifier = Modifier.testTag("ai_streaming_text")
                                )
                            }
                        }

                        is AiForensicsUiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.mode.title.uppercase(),
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("ScrapeGuard AI Notice", state.text)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied AI Notice to Clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .size(28.dp)
                                            .testTag("copy_ai_output_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy text",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = state.text,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    modifier = Modifier.testTag("ai_result_text")
                                )
                            }
                        }

                        is AiForensicsUiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterVertically,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = DangerRed,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "AI Diagnostics Error",
                                    color = DangerRed,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.message,
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                        .testTag("ai_error_message")
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        when (selectedMode) {
                                            AiForensicsMode.HAZARD_DIAGNOSIS -> viewModel.analyzeHazard(hazard, vehicleProfile)
                                            AiForensicsMode.DOT_311_CLAIM -> viewModel.generate311ClaimNotice(hazard, vehicleProfile)
                                            AiForensicsMode.FREEZE_THAW_EXPLANATION -> viewModel.analyzeFreezeThawCycle(currentWeather, historicalHotspots.firstOrNull())
                                            AiForensicsMode.LIVE_TACTICAL_CHAT -> {
                                                if (userCustomQuestion.isNotBlank()) {
                                                    viewModel.askTacticalCopilot(userCustomQuestion, "Retry")
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("ai_retry_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry Diagnostic Request")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close")
                    }

                    if (selectedMode == AiForensicsMode.DOT_311_CLAIM && uiState is AiForensicsUiState.Success) {
                        Button(
                            onClick = {
                                val successState = uiState as? AiForensicsUiState.Success
                                if (successState != null && onApplyClaimToReport != null) {
                                    onApplyClaimToReport(successState.text)
                                    Toast.makeText(context, "Legal Claim Applied to DOT Work Order!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("apply_claim_to_dot_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Attach Notice to DOT 311 Ticket",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
