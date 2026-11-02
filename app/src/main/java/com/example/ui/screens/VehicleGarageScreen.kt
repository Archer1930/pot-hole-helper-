package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FrontLipType
import com.example.data.model.SuspensionType
import com.example.data.model.VehicleProfile
import com.example.data.model.WheelProfile
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkAsphalt
import com.example.ui.theme.LipRed
import com.example.ui.theme.ScrapeAmber
import com.example.ui.theme.SmoothGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VehicleGarageScreen(
    vehicle: VehicleProfile,
    presets: List<VehicleProfile>,
    onUpdateClearance: (Float) -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateSuspension: (SuspensionType) -> Unit,
    onUpdateLip: (FrontLipType) -> Unit,
    onUpdateWheel: (WheelProfile) -> Unit,
    onToggleAirLift: () -> Unit,
    onLoadPreset: (VehicleProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkAsphalt)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VEHICLE CLEARANCE GARAGE",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberCyan
                    )
                    Text(
                        text = "Tune ride height, front lip, and damage risk profile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Damage Prevention Savings Ledger Card
        item {
            ScrapeSavingsLedgerCard(
                scrapesPrevented = vehicle.totalScrapesPrevented,
                moneySaved = vehicle.totalRepairMoneySaved
            )
        }

        // Quick Vehicle Presets Row
        item {
            Text(
                text = "COMMUNITY CLEARANCE PRESETS",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presets) { preset ->
                    val isCurrent = preset.baseClearanceInches == vehicle.baseClearanceInches &&
                            preset.suspensionType == vehicle.suspensionType
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, if (isCurrent) CyberCyan else CardBorder, RoundedCornerShape(10.dp))
                            .clickable { onLoadPreset(preset) },
                        color = if (isCurrent) CyberCyan.copy(alpha = 0.15f) else CardDark
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = preset.vehicleName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isCurrent) CyberCyan else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${preset.baseClearanceInches}\" Ride Height",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Clearance Ruler & Adjustment Slider
        item {
            ClearanceTuningCard(
                vehicle = vehicle,
                onUpdateClearance = onUpdateClearance,
                onToggleAirLift = onToggleAirLift
            )
        }

        // Vehicle Name Input
        item {
            OutlinedTextField(
                value = vehicle.vehicleName,
                onValueChange = onUpdateName,
                label = { Text("Vehicle Name / Build Tag") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        // Suspension System Selector
        item {
            SuspensionSelectorCard(
                currentSuspension = vehicle.suspensionType,
                onSelectSuspension = onUpdateSuspension
            )
        }

        // Front Lip / Splitter Vulnerability Selector
        item {
            FrontLipSelectorCard(
                currentLip = vehicle.frontLipType,
                onSelectLip = onUpdateLip
            )
        }

        // Wheel & Rim Profile Selector
        item {
            WheelProfileCard(
                currentWheel = vehicle.wheelProfile,
                onSelectWheel = onUpdateWheel
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScrapeSavingsLedgerCard(
    scrapesPrevented: Int,
    moneySaved: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SmoothGreen.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = SmoothGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SCRAPE SAVINGS LEDGER",
                        style = MaterialTheme.typography.labelLarge,
                        color = SmoothGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$moneySaved SAVED IN REPAIRS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "$scrapesPrevented deep craters successfully bypassed",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = SmoothGreen,
                modifier = Modifier.size(42.dp)
            )
        }
    }
}

@Composable
private fun ClearanceTuningCard(
    vehicle: VehicleProfile,
    onUpdateClearance: (Float) -> Unit,
    onToggleAirLift: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GROUND CLEARANCE (CHASSIS TO ASPHALT)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${String.format("%.1f", vehicle.effectiveClearanceInches)}\"",
                            style = MaterialTheme.typography.displayLarge,
                            color = if (vehicle.isLiftActive) SmoothGreen else CyberCyan
                        )
                        if (vehicle.isLiftActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(+1.8\" Lifted)",
                                style = MaterialTheme.typography.titleMedium,
                                color = SmoothGreen,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }

                if (vehicle.suspensionType.canLift) {
                    Button(
                        onClick = onToggleAirLift,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (vehicle.isLiftActive) SmoothGreen else CardBorder,
                            contentColor = if (vehicle.isLiftActive) Color.Black else TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("garage_air_lift_button")
                    ) {
                        Text(
                            text = if (vehicle.isLiftActive) "LOWER RIDE" else "RAISE +1.8\"",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clearance Visual Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0C1017))
            ) {
                val fillRatio = ((vehicle.effectiveClearanceInches - 1.5f) / 4.0f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fillRatio)
                        .height(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (vehicle.effectiveClearanceInches < 2.5f) LipRed else if (vehicle.effectiveClearanceInches < 3.5f) ScrapeAmber else SmoothGreen)
                )
                Text(
                    text = "Asphalt Clearance Spectrum",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = vehicle.baseClearanceInches,
                onValueChange = onUpdateClearance,
                valueRange = 1.5f..5.5f,
                colors = SliderDefaults.colors(
                    thumbColor = CyberCyan,
                    activeTrackColor = CyberCyan,
                    inactiveTrackColor = CardBorder
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1.5\" (Slammed)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("3.5\" (Sport Low)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("5.5\" (Stock)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun SuspensionSelectorCard(
    currentSuspension: SuspensionType,
    onSelectSuspension: (SuspensionType) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "SUSPENSION ARCHITECTURE",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SuspensionType.values().take(3).forEach { suspension ->
                    val isSelected = currentSuspension == suspension
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else Color(0xFF0F151F))
                            .border(1.dp, if (isSelected) CyberCyan else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectSuspension(suspension) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = suspension.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) CyberCyan else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FrontLipSelectorCard(
    currentLip: FrontLipType,
    onSelectLip: (FrontLipType) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "FRONT LIP / SPLITTER REPLACEMENT RISK",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FrontLipType.values().forEach { lip ->
                    val isSelected = currentLip == lip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ScrapeAmber.copy(alpha = 0.15f) else Color(0xFF0F151F))
                            .border(1.dp, if (isSelected) ScrapeAmber else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectLip(lip) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lip.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) ScrapeAmber else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = "$${lip.replaceCost} Replacement",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (lip.replaceCost > 800) LipRed else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WheelProfileCard(
    currentWheel: WheelProfile,
    onSelectWheel: (WheelProfile) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "WHEEL & TIRE SIDEWALL PROFILE",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                WheelProfile.values().forEach { wheel ->
                    val isSelected = currentWheel == wheel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color(0xFF0F151F))
                            .border(1.dp, if (isSelected) CyberCyan else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectWheel(wheel) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = wheel.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) CyberCyan else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = wheel.rimRisk,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (wheel == WheelProfile.LOW_PROFILE_30_SERIES) LipRed else TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = "$${wheel.rimRepairCost} Rim Repair",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
