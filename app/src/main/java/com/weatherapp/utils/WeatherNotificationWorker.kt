package com.weatherapp.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WeatherNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WeatherRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = repository.getUserPreferences().first()
        if (!prefs.notificationsEnabled) return Result.success()

        val defaultCity = repository.getDefaultCity().first() ?: return Result.success()
        
        return try {
            val weather = repository.getCurrentWeather(defaultCity.cityName)
            if (weather is Resource.Success) {
                val data = weather.data
                val temp = if (prefs.temperatureUnit == com.weatherapp.domain.model.TemperatureUnit.CELSIUS) 
                    "${data.current.tempC.toInt()}°C" else "${data.current.tempF.toInt()}°F"
                
                notificationHelper.showWeatherNotification(
                    "Weather in ${data.location.name}",
                    "It's ${data.current.condition.text} with $temp.",
                    withSound = true
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
