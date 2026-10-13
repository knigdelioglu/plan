package com.plan.app.core.data

import android.content.Context
import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.ReviewQueue
import kotlinx.serialization.json.Json
import java.io.File

/** Discovers real annual-plan.json files; never redirects a missing school to a different plan. */
class PlanRepository(
    private val context: Context? = null,
    private val plansBaseDir: File? = null
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true }
    private val cache = mutableMapOf<String, AnnualPlan>()
    private val rawCache = mutableMapOf<String, String>()

    data class PlanRef(
        val academicYear: String,
        val courseName: String,
        val gradeLevel: Int,
        val schoolType: String,
        val pathSegment: String,
        val isAvailable: Boolean = true
    )

    // A plan catalog is immutable within a running application; discover bundled assets once.
    private val paths by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { discoverPaths() }
    private val catalog by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        paths.mapNotNull { path -> runCatching {
            val meta = decode(path).metadata
            PlanRef(meta.academicYear, meta.courseName, meta.gradeLevel,
                meta.schoolType, path.removeSuffix("/annual-plan.json"))
        }.getOrNull() }
        .distinctBy { it.pathSegment }
        .sortedWith(compareBy<PlanRef> { it.academicYear }.thenBy { it.gradeLevel }.thenBy { it.schoolType })
    }

    val supportedPlansCatalog: List<PlanRef> get() = availablePlans()
    fun availablePlans(): List<PlanRef> = catalog

    fun loadAnnualPlan(classroom: Classroom): AnnualPlan =
        loadAnnualPlan(classroom.schoolType, classroom.gradeLevel, classroom.academicYear, classroom.courseName)

    fun loadAnnualPlan(
        schoolType: String,
        gradeLevel: Int = 11,
        academicYear: String = "2026-2027",
        courseName: String = "Türk Dili ve Edebiyatı"
    ): AnnualPlan = decode("${resolve(schoolType, gradeLevel, academicYear, courseName).pathSegment}/annual-plan.json")

    fun getRawPlanJson(schoolType: String): String {
        val path = "${resolve(schoolType, 11, "2026-2027", "Türk Dili ve Edebiyatı").pathSegment}/annual-plan.json"
        return rawCache[path] ?: readContent(path).also { rawCache[path] = it }
    }

    fun loadReviewQueue(schoolType: String): ReviewQueue? {
        val ref = runCatching { resolve(schoolType, 11, "2026-2027", "Türk Dili ve Edebiyatı") }.getOrNull()
            ?: return null
        return runCatching {
            json.decodeFromString<ReviewQueue>(readContent("${ref.pathSegment}/review-queue.json"))
        }.getOrNull()
    }

    fun loadValidationReport(schoolType: String): String {
        val ref = runCatching { resolve(schoolType, 11, "2026-2027", "Türk Dili ve Edebiyatı") }.getOrNull()
            ?: return "Plan bulunamadı."
        return runCatching { readContent("${ref.pathSegment}/validation-report.md") }
            .getOrDefault("Doğrulama raporu bulunamadı.")
    }

    private fun resolve(schoolType: String, grade: Int, year: String, course: String): PlanRef =
        availablePlans().firstOrNull {
            it.schoolType.equals(schoolType, ignoreCase = true) &&
                it.gradeLevel == grade && it.academicYear == year &&
                it.courseName.equals(course, ignoreCase = true)
        } ?: throw IllegalStateException("Plan bulunamadı: $year / $course / $grade / $schoolType")

    @Synchronized
    private fun decode(path: String): AnnualPlan {
        cache[path]?.let { return it }
        val raw = readContent(path)
        return json.decodeFromString<AnnualPlan>(raw).also {
            cache[path] = it
            rawCache[path] = raw
        }
    }

    private fun dataRoot(): File? = plansBaseDir ?: if (context == null) {
        listOf(File("../../plans"), File("../plans"), File("plans")).firstOrNull { it.isDirectory }
    } else null

    private fun discoverPaths(): List<String> {
        dataRoot()?.let { root ->
            return root.walkTopDown()
                .filter { it.isFile && it.name == "annual-plan.json" }
                .map { it.relativeTo(root).invariantSeparatorsPath }
                .toList().sorted()
        }
        val assets = context?.assets ?: return emptyList()
        fun collect(dir: String): List<String> =
            assets.list(dir).orEmpty().flatMap { child ->
                val path = if (dir.isEmpty()) child else "$dir/$child"
                if (child == "annual-plan.json") listOf(path) else collect(path)
            }
        return (collect("") + collect("plans"))
            .filter { it.endsWith("/annual-plan.json") }
            .map { it.removePrefix("plans/") }.distinct().sorted()
    }

    private fun readContent(path: String): String {
        dataRoot()?.let {
            val file = File(it, path)
            require(file.isFile) { "Plan verisi bulunamadı: $path" }
            return file.readText(Charsets.UTF_8)
        }
        val assets = checkNotNull(context) { "Plan dizini bulunamadı" }.assets
        return runCatching { assets.open(path) }.recoverCatching { assets.open("plans/$path") }
            .getOrThrow().bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
