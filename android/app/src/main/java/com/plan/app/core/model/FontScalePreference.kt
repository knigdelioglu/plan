package com.plan.app.core.model

/** Integer percentage prevents drift and has a safe default for old saved snapshots. */
object FontScalePreference {
    const val MIN_PERCENT = 90
    const val MAX_PERCENT = 140
    const val STEP_PERCENT = 5
    const val DEFAULT_PERCENT = 115

    fun normalize(percent: Int): Int {
        val bounded = percent.coerceIn(MIN_PERCENT, MAX_PERCENT)
        return ((bounded - MIN_PERCENT + STEP_PERCENT / 2) / STEP_PERCENT) *
            STEP_PERCENT + MIN_PERCENT
    }

    fun isValid(percent: Int): Boolean =
        percent in MIN_PERCENT..MAX_PERCENT &&
            (percent - MIN_PERCENT) % STEP_PERCENT == 0
}
