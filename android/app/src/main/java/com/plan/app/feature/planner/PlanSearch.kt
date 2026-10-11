package com.plan.app.feature.planner

import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.PlanWeek
import java.util.Locale

data class PlanSearchResult(
    val week: PlanWeek,
    val matchingLessons: List<String>
)

/** Searches source-backed plan text without changing or flattening the plan model. */
fun searchPlan(plan: AnnualPlan?, query: String): List<PlanSearchResult> {
    val terms = query.trim().lowercase(TURKISH_LOCALE).split(WHITESPACE).filter(String::isNotBlank)
    if (terms.isEmpty() || plan == null) return emptyList()

    return plan.weeks.mapNotNull { week ->
        val weekFields = buildList {
            add(week.weekNumber.toString())
            add("${week.weekNumber}. hafta")
            add(week.month)
            add(week.dateRangeRaw)
            add(week.theme?.themeCode.orEmpty())
            add(week.theme?.themeName.orEmpty())
            plan.themes.filter { week.weekNumber in it.weekRange }.forEach { theme ->
                add(theme.themeCode)
                add(theme.themeName)
                add(theme.assessmentAndEvaluation?.text.orEmpty())
                theme.interdisciplinaryComponents?.let { components ->
                    listOfNotNull(
                        components.socialEmotionalLearningSkills,
                        components.values,
                        components.literacySkills
                    ).forEach { group ->
                        add(group.text)
                        addAll(group.codes)
                    }
                }
            }
        }
        val lessonMatches = week.lessons.mapNotNull { lesson ->
            val fields = buildList {
                add(lesson.learningArea.orEmpty())
                add(lesson.topicTitle.orEmpty())
                add(lesson.topicRaw.orEmpty())
                addAll(lesson.textsAndMaterials)
                add(lesson.learningOutcomesRaw.orEmpty())
                lesson.learningOutcomes.forEach { outcome ->
                    add(outcome.code)
                    add(outcome.text)
                }
                add(lesson.processComponentsRaw.orEmpty())
                lesson.processComponents.forEach { component ->
                    add(component.mainCode)
                    add(component.title)
                    component.subComponents.forEach { sub ->
                        add(sub.code)
                        add(sub.subCode)
                        add(sub.title)
                        addAll(sub.items)
                    }
                }
            }
            fields.takeIf { fields.matchesAll(terms) }?.let {
                lesson.topicTitle?.takeIf(String::isNotBlank)
                    ?: lesson.learningArea?.takeIf(String::isNotBlank)
                    ?: "${lesson.lessonIndex}. ders bölümü"
            }
        }
        val weekMatches = weekFields.matchesAll(terms)
        if (!weekMatches && lessonMatches.isEmpty()) null
        else PlanSearchResult(week, lessonMatches)
    }
}

private fun List<String>.matchesAll(terms: List<String>): Boolean {
    val searchableText = joinToString(" ").lowercase(TURKISH_LOCALE)
    return terms.all(searchableText::contains)
}

private val TURKISH_LOCALE = Locale.forLanguageTag("tr-TR")
private val WHITESPACE = Regex("\\s+")
