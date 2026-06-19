package com.weatherapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.weatherapp.MainActivity
import com.weatherapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "weather_alerts"
    private val SOUND_CHANNEL_ID = "weather_alerts_sound"

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Weather Alerts"
            val descriptionText = "Morning weather updates and alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            // Channel for notifications with custom sound
            val soundName = "Weather Alerts (With Sound)"
            val soundDescription = "Weather updates with audible alerts"
            val soundImportance = NotificationManager.IMPORTANCE_HIGH
            val soundChannel = NotificationChannel(SOUND_CHANNEL_ID, soundName, soundImportance).apply {
                description = soundDescription
                // Sound is set in the builder for lower APIs, 
                // but for O+ it's managed via the channel.
                // Since I don't have a raw resource yet, I'll use default sound
                // but this is where you'd set the sound via channel.setSound(uri, attributes)
            }
            
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(soundChannel)
        }
    }

    fun showWeatherNotification(title: String, message: String, withSound: Boolean = false) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = if (withSound) SOUND_CHANNEL_ID else CHANNEL_ID
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_weather_default)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (withSound) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            
        if (withSound) {
            // Fallback for older Android versions
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        }

        notificationManager.notify(if (withSound) 2 else 1, builder.build())
    }
}
