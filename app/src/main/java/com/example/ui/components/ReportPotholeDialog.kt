package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.HazardSeverity
import com.example.data.model.HazardType
import com.example.data.model.LanePosition
import com.example.data.model.MunicipalDepartment
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
fun ReportPotholeDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        street: String,
        crossStreet: String,
        depth: Float,
        severity: HazardSeverity,
        type: HazardType,
        lane: LanePosition,
        notes: String,
        photoDrawable: Int?,
        photoUri: String?,
        department: MunicipalDepartment,
        fileMunicipalTicket: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    var streetName by remember { mutableStateOf("Grand Avenue") }
    var crossStreet by remember { mutableStateOf("near 8th Street") }
    var depthInches by remember { mutableFloatStateOf(3.4f) }
    var selectedLane by remember { mutableStateOf(LanePosition.CENTER_LANE) }
    var selectedType by remember { mutableStateOf(HazardType.DEEP_POTHOLE) }
    var selectedDept by remember { mutableStateOf(MunicipalDepartment.CITY_DOT_311) }
    var fileMunicipalTicket by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    // Dashcam photo states
    var selectedPresetPhoto by remember { mutableStateOf<Int?>(R.drawable.dashcam_photo_crater) }
    var customPhotoUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customPhotoUri = uri.toString()
            selectedPresetPhoto = null
        }
    }

    val calculatedSeverity = when {
        depthInches >= 4.0f -> HazardSeverity.CATASTROPHIC
        depthInches >= 2.8f -> HazardSeverity.SEVERE
        depthInches >= 1.5f -> HazardSeverity.MODERATE
        else -> HazardSeverity.MILD
    }

    val severityColor = when (calculatedSeverity) {
        HazardSeverity.CATASTROPHIC, HazardSeverity.SEVERE -> LipRed
        HazardSeverity.MODERATE -> ScrapeAmber
        HazardSeverity.MILD -> SmoothGreen
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REPORT HAZARD & DISPATCH REPAIR",
                    style = MaterialTheme.typography.titleLarge,
                    color = CyberCyan
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Log radar warning for lowered vehicles and dispatch official work orders to city road crews.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Dashcam Photo Attachment Section
                Text(
                    text = "DASHCAM PHOTO EVIDENCE:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Preset 1: Deep Crater
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                2.dp,
                                if (selectedPresetPhoto == R.drawable.dashcam_photo_crater) CyberCyan else CardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedPresetPhoto = R.drawable.dashcam_photo_crater
                                customPhotoUri = null
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.dashcam_photo_crater),
                            contentDescription = "Crater photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color(0xBB000000))
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Crater Snap", fontSize = 9.sp, color = Color.White)
                        }
                    }

                    // Preset 2: Raised Manhole
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                2.dp,
                                if (selectedPresetPhoto == R.drawable.dashcam_photo_manhole) CyberCyan else CardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedPresetPhoto = R.drawable.dashcam_photo_manhole
                                customPhotoUri = null
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.dashcam_photo_manhole),
                            contentDescription = "Manhole photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color(0xBB000000))
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Manhole Snap", fontSize = 9.sp, color = Color.White)
                        }
                    }

                    // Pick Custom / Camera Photo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkAsphalt)
                            .border(
                                2.dp,
                                if (customPhotoUri != null) CyberCyan else CardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (customPhotoUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(customPhotoUri).build(),
                                contentDescription = "Custom photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text("Choose Photo", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = streetName,
                    onValueChange = { streetName = it },
                    label = { Text("Street Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = crossStreet,
                    onValueChange = { crossStreet = it },
                    label = { Text("Cross Street / Landmark") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Depth Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estimated Depth",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${String.format("%.1f", depthInches)}\" (${calculatedSeverity.title})",
                        style = MaterialTheme.typography.labelLarge,
                        color = severityColor
                    )
                }

                Slider(
                    value = depthInches,
                    onValueChange = { depthInches = (it * 10).toInt() / 10f },
                    valueRange = 0.5f..5.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = severityColor,
                        activeTrackColor = severityColor,
                        inactiveTrackColor = CardBorder
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Lane Selector
                Text(
                    text = "Lane Position",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(LanePosition.LEFT_LANE, LanePosition.CENTER_LANE, LanePosition.RIGHT_LANE).forEach { lane ->
                        val isSelected = selectedLane == lane
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else Color(0xFF0E141E))
                                .border(1.dp, if (isSelected) CyberCyan else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedLane = lane }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lane.label.replace(" Lane", ""),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) CyberCyan else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // DOT Department Choice
                Text(
                    text = "Reporting Jurisdiction",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MunicipalDepartment.values().forEach { dept ->
                        val isSelected = selectedDept == dept
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) SmoothGreen.copy(alpha = 0.2f) else Color(0xFF0E141E))
                                .border(1.dp, if (isSelected) SmoothGreen else CardBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedDept = dept }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (dept) {
                                    MunicipalDepartment.CITY_DOT_311 -> "City 311"
                                    MunicipalDepartment.STATE_HIGHWAY_DOT -> "State DOT"
                                    MunicipalDepartment.COUNTY_PUBLIC_WORKS -> "County"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SmoothGreen else TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Also File 311 Ticket Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { fileMunicipalTicket = !fileMunicipalTicket },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = fileMunicipalTicket,
                        onCheckedChange = { fileMunicipalTicket = it },
                        colors = CheckboxDefaults.colors(checkedColor = SmoothGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "File official municipal road repair ticket with ${selectedDept.departmentName.split(" ")[0]}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Driver Statement (Vehicle scrape risk, water ponding)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        streetName,
                        crossStreet,
                        depthInches,
                        calculatedSeverity,
                        selectedType,
                        selectedLane,
                        notes,
                        selectedPresetPhoto,
                        customPhotoUri,
                        selectedDept,
                        fileMunicipalTicket
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (fileMunicipalTicket) "LOG RADAR & FILE 311 TICKET" else "BROADCAST TO RADAR",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}
