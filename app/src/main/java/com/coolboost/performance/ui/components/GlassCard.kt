package com.coolboost.performance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A frosted "glassmorphism" surface: soft translucent gradient fill + subtle
 * light border, used as the base container across the Dashboard and other
 * premium-styled screens.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val topAlpha = if (isDark) 0.10f else 0.65f
    val bottomAlpha = if (isDark) 0.04f else 0.35f
    val borderAlpha = if (isDark) 0.14f else 0.6f

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = topAlpha), Color.White.copy(alpha = bottomAlpha))
                )
            )
            .border(1.dp, Color.White.copy(alpha = borderAlpha), RoundedCornerShape(cornerRadius))
            .padding(contentPadding)
    ) {
        content()
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
