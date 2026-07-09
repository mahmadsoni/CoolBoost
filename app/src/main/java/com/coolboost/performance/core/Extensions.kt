package com.coolboost.performance.core

import kotlin.math.roundToInt

fun Float.roundTo1Decimal(): Float = (this * 10).roundToInt() / 10f

fun Long.bytesToGb(): Double = this / (1024.0 * 1024.0 * 1024.0)

fun Long.bytesToMb(): Double = this / (1024.0 * 1024.0)

fun Double.formatGb(): String = String.format("%.2f GB", this)

fun Double.formatPercent(): String = "${this.roundToInt()}%"

fun Int.coercePercent(): Int = this.coerceIn(0, 100)

fun Float.coercePercent(): Float = this.coerceIn(0f, 100f)
