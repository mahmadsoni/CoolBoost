# CoolBoost Proguard Rules

# Keep Room entities/DAOs
-keep class com.coolboost.performance.data.local.entity.** { *; }
-keep interface com.coolboost.performance.data.local.dao.** { *; }

# Keep domain models (used for state exposure / reflection-free serialization)
-keep class com.coolboost.performance.domain.model.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.flow.**FlowKt

# WorkManager
-keep class androidx.work.impl.background.systemjob.SystemJobService { *; }

# Compose
-keep class androidx.compose.runtime.** { *; }

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
