package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherCondition
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.FrostIce
import com.example.ui.theme.LipRed
import com.example.ui.theme.ScrapeAmber
import com.example.ui.theme.SmoothGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WeatherPickerDialog(
    currentWeather: WeatherCondition,
    onSelectWeather: (WeatherCondition) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Text(
                text = "SELECT WEATHER CONDITION",
                style = MaterialTheme.typography.titleLarge,
                color = FrostIce
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Adverse weather and freeze-thaw cycles dramatically accelerate asphalt erosion and conceal craters.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                WeatherCondition.values().forEach { weather ->
                    val isSelected = currentWeather == weather
                    val icon = when (weather) {
                        WeatherCondition.DRY_MODERATE -> Icons.Default.WbSunny
                        WeatherCondition.FREEZE_THAW_POST_WINTER -> Icons.Default.AcUnit
                        WeatherCondition.HEAVY_RAIN_FLOODED -> Icons.Default.WaterDrop
                        WeatherCondition.MELTING_SLUSH -> Icons.Default.AcUnit
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) FrostIce.copy(alpha = 0.2f) else CardBorder.copy(alpha = 0.2f))
                            .border(1.dp, if (isSelected) FrostIce else CardBorder, RoundedCornerShape(10.dp))
                            .clickable { onSelectWeather(weather) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (weather.isFreezeThawActive) FrostIce else if (weather == WeatherCondition.DRY_MODERATE) SmoothGreen else ScrapeAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = weather.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = weather.tempDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "${weather.hazardMultiplier}x Risk",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (weather.hazardMultiplier > 2.0f) LipRed else if (weather.hazardMultiplier > 1.2f) ScrapeAmber else SmoothGreen
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("DONE", color = FrostIce)
            }
        }
    )
}
