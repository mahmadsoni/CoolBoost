package com.coolboost.performance.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coolboost.performance.domain.model.AiInsight
import com.coolboost.performance.domain.model.InsightSeverity
import com.coolboost.performance.ui.theme.AlertRed
import com.coolboost.performance.ui.theme.SuccessGreen
import com.coolboost.performance.ui.theme.WarningAmber

@Composable
fun InsightCard(insight: AiInsight, modifier: Modifier = Modifier) {
    val (icon, color) = when (insight.severity) {
        InsightSeverity.CRITICAL -> Icons.Filled.Warning to AlertRed
        InsightSeverity.WARNING -> Icons.Filled.Warning to WarningAmber
        InsightSeverity.SUGGESTION -> Icons.Filled.Info to MaterialTheme.colorScheme.primary
        InsightSeverity.INFO -> Icons.Filled.CheckCircle to SuccessGreen
    }

    GlassCard(modifier = modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Row {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .height(36.dp)
                    .width(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.height(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(insight.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(insight.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
