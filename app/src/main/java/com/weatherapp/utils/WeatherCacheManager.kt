package com.weatherapp.utils

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.weatherapp.data.local.db.AppDatabase
import com.weatherapp.utils.WeatherNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {

    suspend fun getCacheSize(): Long = withContext(Dispatchers.IO) {
        // Calculate cache size from database and files
        val dbFile = context.getDatabasePath(Constants.DATABASE_NAME)
        val dbSize = if (dbFile.exists()) dbFile.length() else 0L

        // Add any other cache files
        val cacheDir = context.cacheDir
        val cacheSize = cacheDir.listFiles()?.sumOf { it.length() } ?: 0L

        dbSize + cacheSize
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        // Clear database cache tables
        database.weatherCacheDao().clearAll()
        database.cityDao().deleteAll()

        // Clear cache directory
        val cacheDir = context.cacheDir
        cacheDir.listFiles()?.forEach { it.delete() }

        // Trigger VACUUM to actually reduce file size
        try {
            database.openHelper.writableDatabase.execSQL("VACUUM")
        } catch (e: Exception) {
            // Ignore vacuum errors
        }

        // Schedule periodic cache cleanup
        scheduleCacheCleanup()
    }

    fun scheduleWeatherAlerts() {
        val workManager = WorkManager.getInstance(context)
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        // Schedule at 8am, 12pm, 4pm, 8pm
        val hours = listOf(8, 12, 16, 20)
        
        hours.forEach { hour ->
            val delay = calculateDelay(hour)
            val weatherWork = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()
                
            workManager.enqueueUniquePeriodicWork(
                "weather_alerts_$hour",
                ExistingPeriodicWorkPolicy.REPLACE,
                weatherWork
            )
        }
    }

    private fun calculateDelay(hour: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return calendar.timeInMillis - now
    }

    fun cancelWeatherAlerts() {
        val workManager = WorkManager.getInstance(context)
        listOf(8, 12, 16, 20).forEach { hour ->
            workManager.cancelUniqueWork("weather_alerts_$hour")
        }
        // Also cancel legacy work if any
        workManager.cancelUniqueWork("weather_alerts")
    }

    private fun scheduleCacheCleanup() {
        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val cleanupWork = PeriodicWorkRequestBuilder<CacheCleanupWorker>(
            7, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1, TimeUnit.HOURS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "cache_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWork
        )
    }
}

class CacheCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Clean old cache entries
            val database = AppDatabase.getInstance(applicationContext)
            database.weatherCacheDao().deleteOlderThan(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}