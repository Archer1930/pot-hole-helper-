package com.example

import com.example.data.model.FrontLipType
import com.example.data.model.FreezeThawRisk
import com.example.data.model.HazardSeverity
import com.example.data.model.HazardType
import com.example.data.model.LanePosition
import com.example.data.model.MunicipalDepartment
import com.example.data.model.PotholeHazard
import com.example.data.model.SuspensionType
import com.example.data.model.VehicleProfile
import com.example.data.model.WeatherCondition
import com.example.data.model.WheelProfile
import com.example.data.repository.HazardRepository
import com.example.data.repository.VehicleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testEffectiveClearanceWithAirLift() {
        val slammedCar = VehicleProfile(
            baseClearanceInches = 2.0f,
            suspensionType = SuspensionType.AIR_SUSPENSION,
            isLiftActive = false,
            liftHeightInches = 1.8f
        )
        assertEquals(2.0f, slammedCar.effectiveClearanceInches, 0.01f)

        val liftedCar = slammedCar.copy(isLiftActive = true)
        assertEquals(3.8f, liftedCar.effectiveClearanceInches, 0.01f)
    }

    @Test
    fun testPotholeScrapeRiskCalculation() {
        val deepPothole = PotholeHazard(
            id = "test_1",
            streetName = "Main St",
            crossStreet = "at 1st",
            latitude = 0.0,
            longitude = 0.0,
            severity = HazardSeverity.CATASTROPHIC,
            type = HazardType.DEEP_POTHOLE,
            lane = LanePosition.CENTER_LANE,
            depthInches = 4.0f,
            reportCount = 10,
            scrapeIncidentsLogged = 5,
            historicalRecurrenceRate = 0.9f,
            freezeThawVulnerability = FreezeThawRisk.LOW,
            firstReportedYear = 2022,
            lastVerifiedDaysAgo = 1
        )

        // Lowered car with 2.5" clearance will face maximum scrape risk against 4.0" hole
        val riskLowered = deepPothole.calculateScrapeRisk(2.5f, weatherMultiplier = 1.0f)
        assertEquals(1.0f, riskLowered, 0.01f)

        // Lifted or stock vehicle with 5.5" clearance will clear easily with low freeze-thaw risk
        val riskStock = deepPothole.calculateScrapeRisk(5.5f, weatherMultiplier = 1.0f)
        assertTrue(riskStock < 0.3f)
    }

    @Test
    fun testFreezeThawMultiplierIncreasesRisk() {
        val hazardRepo = HazardRepository()
        hazardRepo.updateWeather(WeatherCondition.FREEZE_THAW_POST_WINTER)
        assertEquals(2.4f, hazardRepo.currentWeather.value.hazardMultiplier, 0.01f)
        assertTrue(hazardRepo.currentWeather.value.isFreezeThawActive)
    }

    @Test
    fun testRouteComparisonDamageSavings() {
        val repo = HazardRepository()
        val comparison = repo.getRouteComparison(2.5f)

        assertTrue(comparison.googleMapsDirectRoute.severePotholesCount > 0)
        assertEquals(0, comparison.scrapeGuardDetourRoute.severePotholesCount)
        assertTrue(comparison.dollarSavings > 0)
    }

    @Test
    fun testMunicipalTicketCreationAndPhotoAttachment() {
        val repo = HazardRepository()
        val initialCount = repo.municipalTickets.value.size

        val hazard = repo.hazards.value.first()
        val ticket = repo.submitMunicipalTicket(
            hazard = hazard,
            department = MunicipalDepartment.CITY_DOT_311,
            driverStatement = "Test pothole damage statement",
            photoDrawable = R.drawable.dashcam_photo_crater,
            photoUri = null
        )

        assertNotNull(ticket.ticketId)
        assertTrue(ticket.ticketId.startsWith("DOT-311-"))
        assertEquals(initialCount + 1, repo.municipalTickets.value.size)
        assertEquals(MunicipalDepartment.CITY_DOT_311, ticket.department)
        assertNotNull(ticket.dashcamPhotoDrawable)
    }

    @Test
    fun testThePotholeWayLaneGuidanceRoute() {
        val repo = HazardRepository()
        val comparison = repo.getRouteComparison(2.5f)

        val potholeWay = comparison.potholeWayLaneGuidanceRoute
        assertEquals("The Pothole Way (Active Lane Guidance)", potholeWay.title)
        assertTrue(potholeWay.isScrapeGuardSafeRoute)
        assertEquals(0, potholeWay.financialRiskEstimateDollars)
        assertTrue(potholeWay.steps.any { it.safeLaneAdvice != null })
    }

    @Test
    fun testObd2ClearanceConversionAndHalfMileThreshold() {
        val obd = com.example.data.model.Obd2Telemetry(
            suspensionClearanceMm = 71 // 2.8 inches
        )
        assertEquals(2.7f, obd.clearanceInches, 0.15f)

        val telemetry = com.example.service.DriveTelemetry()
        assertEquals(2640, telemetry.warningThresholdFeet) // Exactly half a mile
    }
}
