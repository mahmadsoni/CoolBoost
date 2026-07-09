package com.coolboost.performance.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.presentation.viewmodel.AnalyticsViewModel
import com.coolboost.performance.presentation.viewmodel.ViewModelFactory
import com.coolboost.performance.ui.components.GlassCard
import com.coolboost.performance.ui.theme.CoolCyan
import com.coolboost.performance.utils.FormatUtils

@Composable
fun AnalyticsScreen(container: AppContainer) {
    val viewModel: AnalyticsViewModel = viewModel(factory = ViewModelFactory(container))
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Performance Analytics", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("24h Average Health Score", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text("${uiState.averageScore}", style = MaterialTheme.typography.displaySmall, color = CoolCyan)
        }

        Spacer(Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Score Trend", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            TrendChart(values = uiState.history.map { it.performanceScore.toFloat() })
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(title = "Cooling Runs", value = "${uiState.coolingRunsToday}", modifier = Modifier.weight(1f))
            StatCard(title = "RAM Freed", value = FormatUtils.formatBytes(uiState.totalRamFreed), modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(title = "Cache Freed", value = FormatUtils.formatBytes(uiState.totalCacheFreed), modifier = Modifier.weight(1f))
            uiState.battery?.let {
                StatCard(title = "Battery Health", value = "${it.capacityHealthPercent}%", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, cornerRadius = 16.dp) {
        Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun TrendChart(values: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        if (values.size < 2) return@Canvas
        val max = 100f
        val stepX = size.width / (values.size - 1)
        val path = androidx.compose.ui.graphics.Path()
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / max) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = CoolCyan, style = Stroke(width = 4f))
    }
}
