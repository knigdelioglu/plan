package com.plan.app.core.data

import android.content.Context
import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.ReviewQueue
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream

class PlanRepository(
    private val context: Context? = null,
    private val plansBaseDir: File? = null
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val planCache = mutableMapOf<String, AnnualPlan>()
    private val rawJsonCache = mutableMapOf<String, String>()

    data class PlanRef(
        val academicYear: String,
        val courseName: String,
        val gradeLevel: Int,
        val schoolType: String,
        val pathSegment: String,
        val isAvailable: Boolean
    )

    val supportedPlansCatalog = listOf(
        PlanRef("2026-2027", "Türk Dili ve Edebiyatı", 11, "Anadolu Lisesi", "2026-2027/turk-dili-ve-edebiyati/11/anadolu-lisesi", true),
        PlanRef("2026-2027", "Türk Dili ve Edebiyatı", 11, "Fen Lisesi", "2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi", true),
        PlanRef("2026-2027", "Türk Dili ve Edebiyatı", 11, "Sosyal Bilimler Lisesi", "2026-2027/turk-dili-ve-edebiyati/11/sosyal-bilimler-lisesi", false),
        PlanRef("2026-2027", "Türk Dili ve Edebiyatı", 9, "Anadolu Lisesi", "2026-2027/turk-dili-ve-edebiyati/9/anadolu-lisesi", false),
        PlanRef("2026-2027", "Türk Dili ve Edebiyatı", 10, "Anadolu Lisesi", "2026-2027/turk-dili-ve-edebiyati/10/anadolu-lisesi", false),
        PlanRef("2026-2027", "Türk Dili ve Edebiyatı", 12, "Anadolu Lisesi", "2026-2027/turk-dili-ve-edebiyati/12/anadolu-lisesi", false)
    )

    fun loadAnnualPlan(schoolType: String): AnnualPlan {
        val segment = if (schoolType.contains("Fen", ignoreCase = true)) {
            "2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi"
        } else {
            "2026-2027/turk-dili-ve-edebiyati/11/anadolu-lisesi"
        }

        planCache[segment]?.let { return it }

        val filePath = "$segment/annual-plan.json"
        val content = readContent(filePath)
        rawJsonCache[segment] = content
        val plan = json.decodeFromString<AnnualPlan>(content)
        planCache[segment] = plan
        return plan
    }

    fun getRawPlanJson(schoolType: String): String {
        val segment = if (schoolType.contains("Fen", ignoreCase = true)) {
            "2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi"
        } else {
            "2026-2027/turk-dili-ve-edebiyati/11/anadolu-lisesi"
        }
        return rawJsonCache[segment] ?: readContent("$segment/annual-plan.json").also {
            rawJsonCache[segment] = it
        }
    }

    fun loadReviewQueue(schoolType: String): ReviewQueue? {
        val segment = if (schoolType.contains("Fen", ignoreCase = true)) {
            "2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi"
        } else {
            "2026-2027/turk-dili-ve-edebiyati/11/anadolu-lisesi"
        }
        return try {
            val content = readContent("$segment/review-queue.json")
            json.decodeFromString<ReviewQueue>(content)
        } catch (e: Exception) {
            null
        }
    }

    fun loadValidationReport(schoolType: String): String {
        val segment = if (schoolType.contains("Fen", ignoreCase = true)) {
            "2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi"
        } else {
            "2026-2027/turk-dili-ve-edebiyati/11/anadolu-lisesi"
        }
        return try {
            readContent("$segment/validation-report.md")
        } catch (e: Exception) {
            "Doğrulama raporu bulunamadı."
        }
    }

    private fun readContent(relativePath: String): String {
        // Priority 1: Direct File System (JVM Unit Tests or direct dev path)
        if (plansBaseDir != null) {
            val file = File(plansBaseDir, relativePath)
            if (file.exists()) return file.readText(Charsets.UTF_8)
        }

        // Check common relative paths in local testing
        val testPaths = listOf(
            File("../../plans", relativePath),
            File("../plans", relativePath),
            File("plans", relativePath),
            File("/Volumes/Lacie/MacBook Klasörleri/plan/plans", relativePath)
        )
        for (f in testPaths) {
            if (f.exists()) return f.readText(Charsets.UTF_8)
        }

        // Priority 2: Android Assets
        if (context != null) {
            try {
                // In assets, path may be prefixed with plans/ or directly 2026-2027/...
                val stream: InputStream = try {
                    context.assets.open(relativePath)
                } catch (e: Exception) {
                    context.assets.open("plans/$relativePath")
                }
                return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } catch (e: Exception) {
                // fallthrough
            }
        }

        throw IllegalStateException("Kaynak plan verisi bulunamadı: $relativePath")
    }
}
