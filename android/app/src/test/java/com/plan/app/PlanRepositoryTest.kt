package com.plan.app

import com.plan.app.core.data.PlanRepository
import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class PlanRepositoryTest {

    private lateinit var repository: PlanRepository
    private lateinit var teacherRepo: TeacherRepository

    @Before
    fun setUp() {
        val rootDir = File("../../plans")
        val plansDir = if (rootDir.exists()) rootDir else File("plans")
        repository = PlanRepository(plansBaseDir = plansDir)
        teacherRepo = TeacherRepository(context = null)
        teacherRepo.clearAllForTesting()
    }

    @Test
    fun testAnadoluLisesiPlanParsing() {
        val plan = repository.loadAnnualPlan("Anadolu Lisesi")
        assertNotNull("Anadolu Lisesi planı yüklenemedi", plan)

        // 1. 37 hafta
        assertEquals(37, plan.weeks.size)

        // 2. 45 ders bölümü
        val totalLessons = plan.weeks.sumOf { it.lessons.size }
        assertEquals(45, totalLessons)

        // Process details must survive nested JSON decoding.
        val process = plan.weeks.first().lessons.first().processComponents.first()
        assertEquals("TDE2.1", process.mainCode)
        assertEquals("TDE2.1.1", process.subComponents.first().code)
        assertTrue(process.subComponents.first().items.isNotEmpty())

        // 3. Çok bölümlü haftalar (3+2 dağılımı olan 8 hafta)
        val multiWeeks = plan.weeks.filter { it.lessons.size > 1 }
        assertEquals(8, multiWeeks.size)
        multiWeeks.forEach { w ->
            assertEquals(2, w.lessons.size)
            assertEquals(3, w.lessons[0].hours)
            assertEquals(2, w.lessons[1].hours)
            assertEquals(5, w.totalHours)
        }

        // 4. Doğrulanmış ve varsayılan saatler
        assertEquals(180, plan.metadata.verifiedLessonHours)
        assertEquals(5, plan.metadata.assumedLessonHours)
        assertEquals(185, plan.metadata.totalAnnualHoursAssumed)

        // 5. 15 Temmuz Demokrasi Günü açılış haftası yerleşimi
        val week1 = plan.weeks.first { it.weekNumber == 1 }
        val specialDays = week1.specialDaysAndWeeks
        assertNotNull(specialDays)
        assertTrue(specialDays!!.present)
        assertFalse("15 Temmuz takvim uyuşmazlığı olarak etiketlenmemelidir", specialDays.hasCalendarDiscrepancy)
        val event15 = specialDays.events.firstOrNull { it.name.contains("15 Temmuz") }
        assertNotNull(event15)
        assertEquals("intentional_school_year_opening_observance", event15!!.schedulingStatus)
        assertTrue(event15.isScheduleValid)

        // 6. Akademik tatiller
        assertEquals(3, plan.academicCalendarBreaks.size)
        val breakNames = plan.academicCalendarBreaks.map { it.breakName }
        assertTrue(breakNames.contains("1. DÖNEM ARA TATİLİ"))
        assertTrue(breakNames.contains("YARIYIL TATİLİ"))
        assertTrue(breakNames.contains("2. DÖNEM ARA TATİLİ"))

        // 7. 4 Tema
        assertEquals(4, plan.themes.size)
        assertEquals("1.TEMA", plan.themes[0].themeCode)
        assertEquals(45, plan.themes[0].totalHours)
    }

    @Test
    fun testFenLisesiPlanParsing() {
        val plan = repository.loadAnnualPlan("Fen Lisesi")
        assertNotNull("Fen Lisesi planı yüklenemedi", plan)

        // 1. 37 hafta
        assertEquals(37, plan.weeks.size)

        // 2. 45 ders bölümü
        val totalLessons = plan.weeks.sumOf { it.lessons.size }
        assertEquals(45, totalLessons)

        // 3. Çok bölümlü haftalar (3+2)
        val multiWeeks = plan.weeks.filter { it.lessons.size > 1 }
        assertEquals(8, multiWeeks.size)

        // Fen source states 185 total but does not contain an independently
        // verified 180+5 split. Unknown data must remain unknown.
        assertNull(plan.metadata.verifiedLessonHours)
        assertNull(plan.metadata.assumedLessonHours)
        assertNull(plan.metadata.totalAnnualHoursAssumed)
        assertEquals(185, plan.metadata.totalAnnualHours)

        // 5. Okul türü
        assertEquals("Fen Lisesi", plan.metadata.schoolType)
        assertEquals("15 Temmuz Demokrasi ve Millî Birlik Günü",
            plan.weeks.first().specialDaysAndWeeks?.text)
        assertEquals("TDE2.1", plan.weeks.first().lessons.first().processComponents.first().mainCode)
    }

    @Test
    fun testClassroomDataIsolation() {
        val classA = "cls-11a"
        val classB = "cls-11b"

        // Mark lesson 1 of week 1 complete for 11-A only
        teacherRepo.toggleLessonProgress(classA, weekNumber = 1, lessonIndex = 1)
        assertTrue(teacherRepo.isLessonCompleted(classA, 1, 1))
        assertFalse("11-B'nin ilerlemesi 11-A'dan etkilenmemeli", teacherRepo.isLessonCompleted(classB, 1, 1))

        // Save note for 11-A only
        teacherRepo.saveNote(classA, weekNumber = 1, lessonIndex = null, text = "11-A notu")
        val noteA = teacherRepo.getNote(classA, 1, null)
        val noteB = teacherRepo.getNote(classB, 1, null)
        assertEquals("11-A notu", noteA?.text)
        assertEquals("11-B için not bulunmamalı", null, noteB?.text)
    }
}
