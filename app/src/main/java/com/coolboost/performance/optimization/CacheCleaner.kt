package com.coolboost.performance.optimization

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Clears cache that this app is legally permitted to delete without root:
 * its own app cache directories, and orphaned temp files it may have created.
 * Third-party apps' private cache directories are sandboxed by Android and
 * cannot be cleared by another app without root — this is enforced at the
 * OS/filesystem level, not a limitation of this codebase.
 */
class CacheCleaner(private val context: Context) {

    suspend fun clearOrphanCache(): Long = withContext(Dispatchers.IO) {
        var freed = 0L
        freed += deleteDirContents(context.cacheDir, olderThanMs = 60 * 60 * 1000L)
        context.externalCacheDir?.let { freed += deleteDirContents(it, olderThanMs = 60 * 60 * 1000L) }
        freed
    }

    suspend fun clearAllAppCache(): Long = withContext(Dispatchers.IO) {
        var freed = 0L
        freed += deleteDirContents(context.cacheDir, olderThanMs = 0L)
        context.externalCacheDir?.let { freed += deleteDirContents(it, olderThanMs = 0L) }
        context.codeCacheDir.let { freed += deleteDirContents(it, olderThanMs = 0L) }
        freed
    }

    private fun deleteDirContents(dir: File, olderThanMs: Long): Long {
        if (!dir.exists()) return 0L
        var freed = 0L
        val cutoff = System.currentTimeMillis() - olderThanMs
        dir.listFiles()?.forEach { file ->
            try {
                if (file.lastModified() <= cutoff) {
                    val size = fileSizeRecursive(file)
                    if (file.deleteRecursively()) freed += size
                }
            } catch (e: Exception) {
                // Ignore individual file failures, continue cleanup
            }
        }
        return freed
    }

    private fun fileSizeRecursive(file: File): Long {
        return if (file.isDirectory) {
            file.listFiles()?.sumOf { fileSizeRecursive(it) } ?: 0L
        } else {
            file.length()
        }
    }
}
