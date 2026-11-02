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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.FreezeThawRisk
import com.example.data.model.HistoricalHotspot
import com.example.data.model.WeatherCondition
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkAsphalt
import com.example.ui.theme.FrostIce
import com.example.ui.theme.LipRed
import com.example.ui.theme.ScrapeAmber
import com.example.ui.theme.SmoothGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HistoricalAnalyticsScreen(
    historicalHotspots: List<HistoricalHotspot>,
    currentWeather: WeatherCondition,
    onSelectWeather: (WeatherCondition) -> Unit,
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
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HISTORICAL RADAR INTEL",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberCyan
                    )
                    Text(
                        text = "Predictive freeze-thaw and multi-year recurrence analytics",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Active Weather & Freeze-Thaw Multiplier Card
        item {
            WeatherPredictiveCard(
                currentWeather = currentWeather,
                onSelectWeather = onSelectWeather
            )
        }

        // Freeze-Thaw Physics Explanation Card
        item {
            FreezeThawMechanicsCard()
        }

        // Chronic Corridors Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CHRONIC RECURRENCE CORRIDORS",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Text(
                    text = "${historicalHotspots.size} High-Risk Zones",
                    style = MaterialTheme.typography.labelSmall,
                    color = LipRed
                )
            }
        }

        items(historicalHotspots) { hotspot ->
            HotspotCorridorCard(hotspot = hotspot)
        }

        // Historical Stats Summary
        item {
            HistoricalStatsOverviewCard()
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun WeatherPredictiveCard(
    currentWeather: WeatherCondition,
    onSelectWeather: (WeatherCondition) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1A26)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, FrostIce.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currentWeather.isFreezeThawActive) Icons.Default.AcUnit else Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = if (currentWeather.isFreezeThawActive) FrostIce else ScrapeAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WEATHER PREDICTIVE MULTIPLIER",
                        style = MaterialTheme.typography.labelLarge,
                        color = FrostIce
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (currentWeather.isFreezeThawActive) LipRed.copy(alpha = 0.2f) else CardDark)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", currentWeather.hazardMultiplier)}x CRATER EXPANSION RISK",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (currentWeather.isFreezeThawActive) LipRed else SmoothGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${currentWeather.title} • ${currentWeather.tempDescription}",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Weather Selector Chips
            Text(
                text = "SIMULATE CLIMATE CONDITIONS:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WeatherCondition.values().forEach { weather ->
                    val isSelected = currentWeather == weather
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) FrostIce.copy(alpha = 0.25f) else CardDark)
                            .border(1.dp, if (isSelected) FrostIce else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelectWeather(weather) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (weather) {
                                WeatherCondition.DRY_MODERATE -> "Dry"
                                WeatherCondition.FREEZE_THAW_POST_WINTER -> "Freeze/Thaw"
                                WeatherCondition.HEAVY_RAIN_FLOODED -> "Rain"
                                WeatherCondition.MELTING_SLUSH -> "Slush"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) FrostIce else TextSecondary,
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
private fun FreezeThawMechanicsCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "WHY FREEZE-THAW CYCLES DESTROY LOWERED CARS",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberCyan
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "1. Water enters asphalt micro-voids during daytime melts.\n" +
                       "2. Night drop below 32°F freezes the water, which expands 9% by volume, fracturing the binder.\n" +
                       "3. Morning thaw leaves an uncompacted subterranean void.\n" +
                       "4. Vehicle tires collapse the crust into sudden 4\" deep, razor-sharp craters that shatter splitters.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun HotspotCorridorCard(hotspot: HistoricalHotspot) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hotspot.corridorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = hotspot.segmentLimits,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (hotspot.recurrenceRatePercent > 90) LipRed.copy(alpha = 0.2f) else ScrapeAmber.copy(alpha = 0.2f))
                        .border(1.dp, if (hotspot.recurrenceRatePercent > 90) LipRed else ScrapeAmber, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${hotspot.recurrenceRatePercent}% HISTORIC RECURRENCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hotspot.recurrenceRatePercent > 90) LipRed else ScrapeAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PERSISTENCE", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    Text("${hotspot.consecutiveYearsPersisting} Consecutive Years", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("AVG CRATER DEPTH", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    Text("${hotspot.averagePotholeDepthInches}\" Deep", style = MaterialTheme.typography.bodyMedium, color = LipRed, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("SCRAPES LOGGED", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    Text("${hotspot.scrapeReportsThisMonth} this month", style = MaterialTheme.typography.bodyMedium, color = ScrapeAmber)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chronic Cause
            Text(
                text = "Root Cause: ${hotspot.chronicCause}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Predictive Warning Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E1418))
                    .border(1.dp, LipRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = LipRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = hotspot.predictiveWarningText,
                        style = MaterialTheme.typography.labelSmall,
                        color = LipRed,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Safe Alternative Recommendation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Recommended Bypass: ",
                    style = MaterialTheme.typography.labelSmall,
                    color = SmoothGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hotspot.recommendedSafeAlternative,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun HistoricalStatsOverviewCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        color = CardDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "METRO DATASET INSIGHTS",
                style = MaterialTheme.typography.labelLarge,
                color = CyberCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("1,248", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Text("Historical Potholes Logged", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Column {
                    Text("4.2 Days", style = MaterialTheme.typography.headlineMedium, color = LipRed)
                    Text("Avg Winter Patch Lifespan", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Column {
                    Text("91%", style = MaterialTheme.typography.headlineMedium, color = SmoothGreen)
                    Text("Damage Reduction with Detours", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
        }
    }
}
