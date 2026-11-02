package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.MunicipalDepartment
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
fun MunicipalReportDialog(
    hazard: PotholeHazard,
    onDismiss: () -> Unit,
    onSubmitTicket: (hazard: PotholeHazard, department: MunicipalDepartment, statement: String) -> Unit
) {
    val context = LocalContext.current
    var selectedDepartment by remember { mutableStateOf(hazard.jurisdiction) }
    var driverStatement by remember {
        mutableStateOf(
            "Urgent repair requested: Measured ${hazard.depthInches}\" deep crater on ${hazard.streetName} (${hazard.lane.label}). Lowered vehicles are bottoming out on chassis and risking oil pan puncture. Dashcam evidence attached."
        )
    }
    var isSubmittedSuccess by remember { mutableStateOf(hazard.municipalTicketId != null) }
    var generatedTicketId by remember { mutableStateOf(hazard.municipalTicketId ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
            color = CardDark
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "MUNICIPAL DOT ROAD REPAIR",
                                style = MaterialTheme.typography.titleLarge,
                                color = CyberCyan
                            )
                            Text(
                                text = "File official work order with road maintenance authority",
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

                Spacer(modifier = Modifier.height(12.dp))

                if (isSubmittedSuccess) {
                    // Success Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F241C)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SmoothGreen)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SmoothGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "OFFICIAL 311 TICKET LOGGED",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SmoothGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Work Order #$generatedTicketId",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Dispatched to ${selectedDepartment.departmentName}. SLA: ${selectedDepartment.targetTurnaround}.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Department Selector
                Text(
                    text = "SELECT RESPONSIBLE JURISDICTION:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                MunicipalDepartment.values().forEach { dept ->
                    val isSelected = selectedDepartment == dept
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color(0xFF0F151F))
                            .border(1.dp, if (isSelected) CyberCyan else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedDepartment = dept }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = dept.departmentName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) CyberCyan else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "${dept.jurisdictionType} • SLA: ${dept.targetTurnaround}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hazard Specs & Dashcam Photo Thumbnail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0E14), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Photo Thumbnail
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                    ) {
                        val photoDrawable = hazard.dashcamPhotoDrawable ?: R.drawable.dashcam_photo_crater
                        Image(
                            painter = painterResource(id = photoDrawable),
                            contentDescription = "Attached dashcam evidence",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(Color(0xCC000000), RoundedCornerShape(topStart = 4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("PHOTO", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = CyberCyan)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${hazard.streetName} at ${hazard.crossStreet}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "GPS: ${String.format("%.4f", hazard.latitude)}°N, ${String.format("%.4f", hazard.longitude)}°W",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${hazard.depthInches}\" Deep • ${hazard.lane.label} • ${hazard.type.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ScrapeAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Driver Statement Input
                OutlinedTextField(
                    value = driverStatement,
                    onValueChange = { driverStatement = it },
                    label = { Text("Driver Description / Damage Statement") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Submit Ticket / Email / Call Hotline
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Submit Digital Work Order
                    Button(
                        onClick = {
                            val ticket = "DOT-311-${(10000..99999).random()}"
                            generatedTicketId = ticket
                            isSubmittedSuccess = true
                            onSubmitTicket(hazard, selectedDepartment, driverStatement)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_dot_ticket_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SmoothGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSubmittedSuccess) "RE-SUBMIT / UPDATE WORK ORDER" else "SUBMIT OFFICIAL 311 WORK ORDER",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Email DOT Dispatch
                    OutlinedButton(
                        onClick = {
                            sendDotDispatchEmail(context, selectedDepartment, hazard, driverStatement)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("email_dot_dispatch_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EMAIL ${selectedDepartment.dispatchEmail.uppercase()}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Direct Phone Hotline
                    OutlinedButton(
                        onClick = {
                            callDotHotline(context, selectedDepartment.hotlinePhone)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = ScrapeAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CALL HOTLINE (${selectedDepartment.hotlinePhone})",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

private fun sendDotDispatchEmail(
    context: Context,
    department: MunicipalDepartment,
    hazard: PotholeHazard,
    statement: String
) {
    try {
        val subject = "[URGENT ROAD HAZARD] Pothole Repair Ticket - ${hazard.streetName} (${hazard.depthInches}\" Depth)"
        val emailBody = """
ATTN: ${department.departmentName}
Road Maintenance & Pothole Repair Dispatch

HAZARD LOCATION:
Street: ${hazard.streetName}
Cross Street / Landmark: ${hazard.crossStreet}
GPS Coordinates: ${hazard.latitude}° N, ${hazard.longitude}° W

HAZARD SPECIFICATIONS:
Measured Depth: ${hazard.depthInches} inches
Lane Position: ${hazard.lane.label}
Hazard Classification: ${hazard.type.displayName}
Severity Level: ${hazard.severity.title}

DRIVER IMPACT REPORT:
$statement

COMMUNITY TELEMETRY:
Documented via ScrapeGuard Lowered Vehicle Road Safety Radar.
Dashcam evidence photo captured with verified coordinates.
Immediate asphalt patch or resurfacing requested to prevent vehicle chassis damage.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(department.dispatchEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, emailBody)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Send DOT Repair Dispatch"))
    } catch (_: Exception) {}
}

private fun callDotHotline(context: Context, phoneNumber: String) {
    try {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phoneNumber.replace("-", "")}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(dialIntent)
    } catch (_: Exception) {}
}
