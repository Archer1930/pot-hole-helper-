package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.DotReportStatus
import com.example.data.model.FreezeThawRisk
import com.example.data.model.HazardSeverity
import com.example.data.model.HazardType
import com.example.data.model.LanePosition
import com.example.data.model.MunicipalDepartment
import com.example.data.model.MunicipalReportTicket
import com.example.data.model.PotholeHazard
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreHazardRepo"

class FirestoreHazardRepository(private val db: FirebaseFirestore) {

    // Convenience constructor resolving the named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun observePotholes(): Flow<List<PotholeHazard>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("potholes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Snapshot error observing potholes", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { doc ->
                            try {
                                val street = doc.getString("street") ?: return@mapNotNull null
                                val cross = doc.getString("crossStreet") ?: ""
                                val depth = (doc.getDouble("depthInches") ?: 2.5).toFloat()
                                val sevStr = doc.getString("severity") ?: HazardSeverity.SEVERE.name
                                val typeStr = doc.getString("type") ?: HazardType.DEEP_POTHOLE.name
                                val laneStr = doc.getString("lane") ?: LanePosition.CENTER_LANE.name
                                val notes = doc.getString("notes") ?: ""
                                val photo = doc.getString("photoUrl")
                                val deptStr = doc.getString("department") ?: MunicipalDepartment.CITY_DOT_311.name

                                val severity = try { HazardSeverity.valueOf(sevStr) } catch (_: Exception) { HazardSeverity.SEVERE }
                                val type = try { HazardType.valueOf(typeStr) } catch (_: Exception) { HazardType.DEEP_POTHOLE }
                                val lane = try { LanePosition.valueOf(laneStr) } catch (_: Exception) { LanePosition.CENTER_LANE }
                                val dept = try { MunicipalDepartment.valueOf(deptStr) } catch (_: Exception) { MunicipalDepartment.CITY_DOT_311 }

                                PotholeHazard(
                                    id = doc.id,
                                    streetName = street,
                                    crossStreet = cross,
                                    city = "Central Metro",
                                    latitude = 37.7749 + (doc.id.hashCode() % 100) * 0.0001,
                                    longitude = -122.4194 + (doc.id.hashCode() % 100) * 0.0001,
                                    severity = severity,
                                    type = type,
                                    lane = lane,
                                    depthInches = depth,
                                    reportCount = 1,
                                    scrapeIncidentsLogged = 0,
                                    historicalRecurrenceRate = 0.85f,
                                    freezeThawVulnerability = FreezeThawRisk.HIGH,
                                    firstReportedYear = 2026,
                                    lastVerifiedDaysAgo = 0,
                                    isHiddenByWaterInRain = false,
                                    communityNotes = notes,
                                    dashcamPhotoDrawable = null,
                                    customPhotoUri = photo,
                                    jurisdiction = dept,
                                    municipalTicketId = null
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error mapping pothole document ${doc.id}", e)
                                null
                            }
                        }
                        trySend(items)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach snapshot listener", e)
        }

        awaitClose { listener?.remove() }
    }

    suspend fun savePothole(hazard: PotholeHazard, userId: String) {
        val data = hashMapOf(
            "userId" to userId,
            "street" to hazard.streetName,
            "crossStreet" to hazard.crossStreet,
            "depthInches" to hazard.depthInches.toDouble(),
            "severity" to hazard.severity.name,
            "type" to hazard.type.name,
            "lane" to hazard.lane.name,
            "evasiveLaneAdvice" to "Avoid ${hazard.lane.name} dip",
            "notes" to hazard.communityNotes,
            "department" to hazard.jurisdiction.name,
            "photoUrl" to (hazard.customPhotoUri ?: "")
        )
        db.collection("potholes").document(hazard.id).set(data).await()
    }

    suspend fun submitDotTicket(ticket: MunicipalReportTicket, userId: String) {
        val data = hashMapOf(
            "userId" to userId,
            "potholeId" to ticket.potholeId,
            "ticketNumber" to ticket.ticketId,
            "department" to ticket.department.name,
            "status" to ticket.status.name,
            "driverStatement" to ticket.driverStatement
        )
        db.collection("dot_tickets").document(ticket.ticketId).set(data).await()
    }
}
