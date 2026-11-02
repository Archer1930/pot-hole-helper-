package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouteComparison
import com.example.data.model.RouteOption
import com.example.data.model.VehicleProfile
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
fun RouteCompareScreen(
    routeComparison: RouteComparison,
    vehicle: VehicleProfile,
    onLaunchGoogleMapsSafeRoute: () -> Unit,
    onNavigatePotholeWay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedRouteOption by remember { mutableStateOf(routeComparison.potholeWayLaneGuidanceRoute) }

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
                        text = "SMART ROUTE & LANE NAVIGATOR",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberCyan
                    )
                    Text(
                        text = "${routeComparison.origin} → ${routeComparison.destination}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.AltRoute,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Damage Savings Banner
        item {
            SavingsRoiCard(
                dollarSavings = routeComparison.dollarSavings,
                timeDifference = routeComparison.timeDifferenceMinutes,
                vehicle = vehicle
            )
        }

        // Route Options (The Pothole Way vs Safe Detour vs Google Direct)
        item {
            Text(
                text = "CHOOSE YOUR ROUTE STRATEGY",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Option 1: The Pothole Way (Active Lane Guidance) - Fast Direct + Evasive Maneuvers
            RouteOptionCard(
                option = routeComparison.potholeWayLaneGuidanceRoute,
                isSelected = selectedRouteOption.id == routeComparison.potholeWayLaneGuidanceRoute.id,
                onClick = { selectedRouteOption = routeComparison.potholeWayLaneGuidanceRoute },
                accentColor = CyberCyan
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: ScrapeGuard Safe Detour (Zero Craters, Resurfaced Roads)
            RouteOptionCard(
                option = routeComparison.scrapeGuardDetourRoute,
                isSelected = selectedRouteOption.id == routeComparison.scrapeGuardDetourRoute.id,
                onClick = { selectedRouteOption = routeComparison.scrapeGuardDetourRoute },
                accentColor = SmoothGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 3: Google Maps Direct Route Card (Dangerous, No Guidance)
            RouteOptionCard(
                option = routeComparison.googleMapsDirectRoute,
                isSelected = selectedRouteOption.id == routeComparison.googleMapsDirectRoute.id,
                onClick = { selectedRouteOption = routeComparison.googleMapsDirectRoute },
                accentColor = LipRed
            )
        }

        // Navigation Action CTA Button
        item {
            val isPotholeWay = selectedRouteOption.id == routeComparison.potholeWayLaneGuidanceRoute.id
            val isSafeDetour = selectedRouteOption.id == routeComparison.scrapeGuardDetourRoute.id

            Button(
                onClick = {
                    if (isPotholeWay) {
                        onNavigatePotholeWay()
                    } else {
                        onLaunchGoogleMapsSafeRoute()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("launch_route_cta_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isPotholeWay -> CyberCyan
                        isSafeDetour -> SmoothGreen
                        else -> LipRed
                    },
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isPotholeWay) Icons.Default.Navigation else Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isPotholeWay -> "START THE POTHOLE WAY (LANE GUIDANCE)"
                        isSafeDetour -> "LAUNCH SMOOTH DETOUR IN GOOGLE MAPS"
                        else -> "LAUNCH DIRECT ROUTE (HIGH RISK)"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 14.sp
                )
            }
        }

        // Turn-by-Turn Guidance Steps
        item {
            Text(
                text = "${selectedRouteOption.title.uppercase()} STEPS",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(selectedRouteOption.steps) { step ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (step.isPotholeZone) LipRed.copy(alpha = 0.6f) else CardBorder,
                        RoundedCornerShape(10.dp)
                    ),
                color = if (step.isPotholeZone) Color(0x22FF2D55) else CardDark
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (step.isPotholeZone) Icons.Default.Dangerous else Icons.Default.Navigation,
                        contentDescription = null,
                        tint = if (step.isPotholeZone) LipRed else CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.instruction,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (step.isPotholeZone) FontWeight.Bold else FontWeight.Normal,
                            color = if (step.isPotholeZone) LipRed else TextPrimary
                        )
                        if (step.safeLaneAdvice != null) {
                            Text(
                                text = "LANE ADVICE: ${step.safeLaneAdvice}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ScrapeAmber
                            )
                        }
                    }
                    Text(
                        text = step.distanceText,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SavingsRoiCard(
    dollarSavings: Int,
    timeDifference: Int,
    vehicle: VehicleProfile
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1E19)
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SmoothGreen.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = SmoothGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POTENTIAL DAMAGE SAVINGS",
                        style = MaterialTheme.typography.labelLarge,
                        color = SmoothGreen
                    )
                }
                Text(
                    text = "+$dollarSavings SAVED",
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 24.sp,
                    color = SmoothGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Adding +$timeDifference min protects your ${vehicle.frontLipType.label} ($${vehicle.frontLipType.replaceCost}) and ${vehicle.wheelProfile.label} ($${vehicle.wheelProfile.rimRepairCost}) from 4 severe craters.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun RouteOptionCard(
    option: RouteOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                2.dp,
                if (isSelected) accentColor else CardBorder,
                RoundedCornerShape(14.dp)
            ),
        color = CardDark,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) accentColor else TextPrimary
                    )
                    Text(
                        text = option.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                if (option.isScrapeGuardSafeRoute) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SmoothGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "RECOMMENDED",
                            style = MaterialTheme.typography.labelSmall,
                            color = SmoothGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(label = "TIME", value = "${option.estimatedTimeMinutes} min")
                MetricColumn(label = "DISTANCE", value = "${option.distanceMiles} mi")
                MetricColumn(
                    label = "HAZARDS",
                    value = "${option.severePotholesCount} Severe",
                    valueColor = if (option.severePotholesCount > 0) LipRed else SmoothGreen
                )
                MetricColumn(
                    label = "SCRAPE RISK",
                    value = "${option.overallScrapeRiskPercent}%",
                    valueColor = if (option.overallScrapeRiskPercent > 30) LipRed else SmoothGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = option.roadConditionSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}
