package com.weatherapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.model.UserPreferences
import com.weatherapp.domain.model.WindSpeedUnit

@Entity(tableName = "user_preferences")
data class PreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1, // Single row
    val temperatureUnit: String = TemperatureUnit.CELSIUS.name,
    val windSpeedUnit: String = WindSpeedUnit.KPH.name,
    val themeMode: String = ThemeMode.SYSTEM.name,
    val notificationsEnabled: Boolean = true,
    val defaultCityId: Int? = null
) {
    fun toDomain(): UserPreferences {
        return UserPreferences(
            temperatureUnit = TemperatureUnit.valueOf(temperatureUnit),
            windSpeedUnit = WindSpeedUnit.valueOf(windSpeedUnit),
            themeMode = ThemeMode.valueOf(themeMode),
            notificationsEnabled = notificationsEnabled,
            defaultCityId = defaultCityId
        )
    }

    companion object {
        fun fromDomain(preferences: UserPreferences): PreferencesEntity {
            return PreferencesEntity(
                temperatureUnit = preferences.temperatureUnit.name,
                windSpeedUnit = preferences.windSpeedUnit.name,
                themeMode = preferences.themeMode.name,
                notificationsEnabled = preferences.notificationsEnabled,
                defaultCityId = preferences.defaultCityId
            )
        }

        fun getDefault(): PreferencesEntity {
            return PreferencesEntity()
        }
    }
}