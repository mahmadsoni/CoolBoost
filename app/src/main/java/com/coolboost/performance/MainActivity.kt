package com.coolboost.performance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.services.MonitoringService
import com.coolboost.performance.ui.navigation.CoolBoostNavGraph
import com.coolboost.performance.ui.theme.CoolBoostTheme

class MainActivity : ComponentActivity() {

    private lateinit var container: AppContainer

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: monitoring still works without notification visibility */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = (application as CoolBoostApp).container

        requestNotificationPermissionIfNeeded()
        startMonitoringService()

        setContent {
            val settings by container.settingsRepository.observeSettings().collectAsState(
                initial = com.coolboost.performance.domain.repository.AppSettings()
            )

            CoolBoostTheme(
                darkTheme = settings.darkModeEnabled,
                dynamicColor = settings.dynamicColorEnabled
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CoolBoostNavGraph(
                        container = container,
                        onRequestUsageAccess = { openUsageAccessSettings() }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startMonitoringService() {
        val intent = Intent(this, MonitoringService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun openUsageAccessSettings() {
        startActivity(com.coolboost.performance.utils.PermissionUtils.usageAccessSettingsIntent())
    }
}
