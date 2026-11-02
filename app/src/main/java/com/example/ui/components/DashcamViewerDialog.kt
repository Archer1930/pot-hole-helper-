package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.HazardSeverity
import com.example.data.model.PotholeHazard
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
fun DashcamViewerDialog(
    hazard: PotholeHazard,
    onDismiss: () -> Unit,
    onReportToDot: (PotholeHazard) -> Unit
) {
    val context = LocalContext.current
    val severityColor = when (hazard.severity) {
        HazardSeverity.CATASTROPHIC, HazardSeverity.SEVERE -> LipRed
        HazardSeverity.MODERATE -> ScrapeAmber
        HazardSeverity.MILD -> SmoothGreen
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
            color = CardDark
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DASHCAM HAZARD EVIDENCE",
                                style = MaterialTheme.typography.titleLarge,
                                color = CyberCyan
                            )
                            Text(
                                text = "${hazard.streetName} ${hazard.crossStreet}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dashcam Photo Viewfinder with HUD Telemetry Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                ) {
                    // Image Loader: If drawable is available, or custom URI, or fallback
                    if (hazard.dashcamPhotoDrawable != null) {
                        Image(
                            painter = painterResource(id = hazard.dashcamPhotoDrawable),
                            contentDescription = "Dashcam capture of pothole",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    } else if (!hazard.customPhotoUri.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(hazard.customPhotoUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "User dashcam photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        // Fallback default realistic dashcam capture
                        Image(
                            painter = painterResource(id = R.drawable.dashcam_photo_crater),
                            contentDescription = "Dashcam photo default",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    // Dashcam Telemetry Stamp Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color(0xBB000000), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LipRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "REC | DASHCAM 60FPS",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${String.format("%.4f", hazard.latitude)}°N ${String.format("%.4f", hazard.longitude)}°W",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "SPEED: 31 MPH • ${hazard.lane.label.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }

                    // Depth & Impact Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color(0xDD000000), RoundedCornerShape(6.dp))
                            .border(1.dp, severityColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${hazard.depthInches}\" MEASURED DEPTH (${hazard.severity.title.uppercase()})",
                            style = MaterialTheme.typography.labelSmall,
                            color = severityColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detail Specs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(DarkAsphalt, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("JURISDICTION", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                        Text(hazard.jurisdiction.departmentName, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 1)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(DarkAsphalt, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("311 WORK ORDER", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                        Text(
                            text = hazard.municipalTicketId ?: "Unreported to DOT",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (hazard.municipalTicketId != null) SmoothGreen else ScrapeAmber
                        )
                    }
                }

                if (hazard.communityNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${hazard.communityNotes}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Button(
                    onClick = {
                        onDismiss()
                        onReportToDot(hazard)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hazard.municipalTicketId != null) "VIEW DOT 311 WORK ORDER" else "DISPATCH TO CITY DOT (311)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
