package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.FreezeThawRisk
import com.example.data.model.HazardSeverity
import com.example.data.model.HazardType
import com.example.data.model.LanePosition
import com.example.data.model.MunicipalDepartment
import com.example.data.model.PotholeHazard
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreHazardRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun authenticatedDriver_canSaveAndReadPothole() = runBlocking {
    val uid = signInTestUser("lowered_driver@example.com")
    val repo = FirestoreHazardRepository(firestore)

    val hazard = PotholeHazard(
      id = "hz_test_101",
      streetName = "Grand Avenue",
      crossStreet = "at 8th St",
      city = "Metro",
      latitude = 37.7749,
      longitude = -122.4194,
      severity = HazardSeverity.CATASTROPHIC,
      type = HazardType.DEEP_POTHOLE,
      lane = LanePosition.CENTER_LANE,
      depthInches = 4.2f,
      reportCount = 1,
      scrapeIncidentsLogged = 0,
      historicalRecurrenceRate = 0.9f,
      freezeThawVulnerability = FreezeThawRisk.EXTREME,
      firstReportedYear = 2026,
      lastVerifiedDaysAgo = 0,
      communityNotes = "Deep center crater",
      jurisdiction = MunicipalDepartment.CITY_DOT_311
    )

    repo.savePothole(hazard, uid)

    val doc = firestore.collection("potholes").document("hz_test_101").get().await()
    assertTrue(doc.exists())
    assertEquals("Grand Avenue", doc.getString("street"))
    assertEquals(uid, doc.getString("userId"))
  }

  @Test
  fun unauthenticatedUser_cannotWritePothole() = runBlocking {
    auth.signOut()
    val repo = FirestoreHazardRepository(firestore)

    val hazard = PotholeHazard(
      id = "hz_unauthed_999",
      streetName = "Unauth Street",
      crossStreet = "None",
      city = "Metro",
      latitude = 37.77,
      longitude = -122.41,
      severity = HazardSeverity.SEVERE,
      type = HazardType.DEEP_POTHOLE,
      lane = LanePosition.LEFT_LANE,
      depthInches = 3.5f,
      reportCount = 1,
      scrapeIncidentsLogged = 0,
      historicalRecurrenceRate = 0.5f,
      freezeThawVulnerability = FreezeThawRisk.LOW,
      firstReportedYear = 2026,
      lastVerifiedDaysAgo = 0,
      communityNotes = "Unauthorized",
      jurisdiction = MunicipalDepartment.CITY_DOT_311
    )

    try {
      repo.savePothole(hazard, "fake_uid")
      fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
    } catch (t: Throwable) {
      val fse = t.unwrapFirestoreException()
      assertNotNull("Expected Firestore exception", fse)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fse?.code)
    }
  }
}
