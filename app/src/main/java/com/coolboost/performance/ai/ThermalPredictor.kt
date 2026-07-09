package com.coolboost.performance.ai

import com.coolboost.performance.domain.model.ThermalPrediction
import kotlin.math.abs

/**
 * Lightweight on-device linear-trend predictor: fits a simple slope over the
 * last N battery-temperature samples to estimate whether the device is
 * heading toward the danger threshold, and roughly when. This is a
 * transparent heuristic model (no cloud calls, fully offline) rather than a
 * trained neural network — appropriate for a privacy-first, offline-first app.
 */
class ThermalPredictor {

    private val samples = ArrayDeque<Pair<Long, Float>>() // timestamp, tempC
    private val maxSamples = 20

    fun addSample(timestampMs: Long, tempCelsius: Float) {
        samples.addLast(timestampMs to tempCelsius)
        if (samples.size > maxSamples) samples.removeFirst()
    }

    fun predict(dangerThreshold: Float): ThermalPrediction {
        if (samples.size < 4) {
            return ThermalPrediction(false, 0, null, 0f)
        }

        val slopePerMinute = computeSlopePerMinute()
        val latestTemp = samples.last().second

        if (slopePerMinute <= 0.02f) {
            return ThermalPrediction(false, 90, null, slopePerMinute)
        }

        val degreesRemaining = dangerThreshold - latestTemp
        if (degreesRemaining <= 0) {
            return ThermalPrediction(true, 95, 0L, slopePerMinute)
        }

        val minutesToThreshold = degreesRemaining / slopePerMinute
        val confidence = (60 + (minutesToThreshold.let { 30 - abs(it - 5) }).coerceIn(0.0, 30.0)).toInt()

        return ThermalPrediction(
            willOverheat = minutesToThreshold in 0.0..15.0,
            confidencePercent = confidence.coerceIn(0, 99),
            etaSeconds = (minutesToThreshold * 60).toLong().coerceAtLeast(0),
            trendSlopePerMinute = slopePerMinute
        )
    }

    private fun computeSlopePerMinute(): Float {
        val first = samples.first()
        val last = samples.last()
        val minutesElapsed = (last.first - first.first) / 60000.0
        if (minutesElapsed <= 0.0) return 0f
        return ((last.second - first.second) / minutesElapsed).toFloat()
    }
}
