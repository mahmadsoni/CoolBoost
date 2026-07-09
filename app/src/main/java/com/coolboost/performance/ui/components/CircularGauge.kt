package com.coolboost.performance.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A large circular progress gauge used for the main "Performance Score" and
 * "Temperature" readouts on the Dashboard, animated smoothly on value change.
 */
@Composable
fun CircularGauge(
    progress: Float, // 0f..1f
    label: String,
    valueText: String,
    color: Color,
    modifier: Modifier = Modifier,
    gaugeSize: Dp = 180.dp,
    strokeWidth: Dp = 14.dp
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animatedProgress.animateTo(progress.coerceIn(0f, 1f), animationSpec = tween(700))
    }

    Box(modifier = modifier.size(gaugeSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(gaugeSize)) {
            val strokePx = strokeWidth.toPx()
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)
            val diameter = this.size.minDimension - strokePx
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                size = Size(diameter, diameter),
                topLeft = androidx.compose.ui.geometry.Offset(strokePx / 2, strokePx / 2)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                style = stroke,
                size = Size(diameter, diameter),
                topLeft = androidx.compose.ui.geometry.Offset(strokePx / 2, strokePx / 2)
            )
        }
        Box(contentAlignment = Alignment.Center) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(valueText, fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
