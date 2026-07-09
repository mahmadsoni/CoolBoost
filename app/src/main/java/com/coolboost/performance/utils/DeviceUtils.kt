package com.coolboost.performance.utils

import android.content.Context
import android.os.Build

object DeviceUtils {

    fun deviceLabel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    fun androidVersionLabel(): String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    /** Rough capability tier used to scale monitoring frequency on low-end devices. */
    fun isLowEndDevice(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        return am.isLowRamDevice || Runtime.getRuntime().availableProcessors() <= 4
    }
}
