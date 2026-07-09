package com.coolboost.performance.monitoring

import android.view.Choreographer
import com.coolboost.performance.core.Constants
import com.coolboost.performance.domain.model.FpsState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

/**
 * Real-time FPS measurement using Choreographer.FrameCallback, the same
 * mechanism used by the Android platform's own frame scheduler. This measures
 * actual rendered frame cadence of the app's own UI thread — the standard,
 * accurate way to measure FPS/jank without root or hidden APIs.
 */
class FpsMonitor {

    fun observe(): Flow<FpsState> = callbackFlow {
        val choreographer = Choreographer.getInstance()
        var lastFrameTimeNanos = 0L
        var frameCount = 0
        var jankCount = 0
        var windowStartNanos = 0L
        val recentFps = ArrayDeque<Double>()

        val callback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (lastFrameTimeNanos != 0L) {
                    val deltaNanos = frameTimeNanos - lastFrameTimeNanos
                    val instantFps = 1_000_000_000.0 / deltaNanos.coerceAtLeast(1)
                    if (instantFps < Constants.FPS_JANK_THRESHOLD) jankCount++

                    frameCount++
                    if (windowStartNanos == 0L) windowStartNanos = frameTimeNanos

                    // Emit an aggregated reading roughly once per second window
                    val windowElapsedMs = (frameTimeNanos - windowStartNanos) / 1_000_000
                    if (windowElapsedMs >= 1000) {
                        val currentFps = instantFps.coerceAtMost(Constants.TARGET_FPS)
                        recentFps.addLast(currentFps)
                        if (recentFps.size > 10) recentFps.removeFirst()
                        val avg = recentFps.average()

                        trySend(
                            FpsState(
                                currentFps = currentFps,
                                averageFps = avg,
                                jankCount = jankCount,
                                isSmooth = avg >= Constants.FPS_JANK_THRESHOLD
                            )
                        )
                        frameCount = 0
                        windowStartNanos = frameTimeNanos
                    }
                }
                lastFrameTimeNanos = frameTimeNanos
                choreographer.postFrameCallback(this)
            }
        }

        choreographer.postFrameCallback(callback)
        awaitClose { choreographer.removeFrameCallback(callback) }
    }.conflate()
}
