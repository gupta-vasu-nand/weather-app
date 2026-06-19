package com.weatherapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {

    private object PreferencesKeys {
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DEFAULT_CITY_ID = intPreferencesKey("default_city_id")
        val LAST_REFRESH_TIME = longPreferencesKey("last_refresh_time")
    }

    val temperatureUnit: Flow<TemperatureUnit> = context.dataStore.data
        .map { preferences ->
            val unitName = preferences[PreferencesKeys.TEMPERATURE_UNIT] ?: TemperatureUnit.CELSIUS.name
            try {
                TemperatureUnit.valueOf(unitName)
            } catch (e: IllegalArgumentException) {
                TemperatureUnit.CELSIUS
            }
        }

    val windSpeedUnit: Flow<WindSpeedUnit> = context.dataStore.data
        .map { preferences ->
            val unitName = preferences[PreferencesKeys.WIND_SPEED_UNIT] ?: WindSpeedUnit.KPH.name
            try {
                WindSpeedUnit.valueOf(unitName)
            } catch (e: IllegalArgumentException) {
                WindSpeedUnit.KPH
            }
        }

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            val modeName = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(modeName)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: false
        }

    val defaultCityId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DEFAULT_CITY_ID]
        }

    val lastRefreshTime: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_REFRESH_TIME] ?: 0L
        }

    suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEMPERATURE_UNIT] = unit.name
        }
    }

    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WIND_SPEED_UNIT] = unit.name
        }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun toggleNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDefaultCityId(cityId: Int?) {
        context.dataStore.edit { preferences ->
            if (cityId != null) {
                preferences[PreferencesKeys.DEFAULT_CITY_ID] = cityId
            } else {
                preferences.remove(PreferencesKeys.DEFAULT_CITY_ID)
            }
        }
    }

    suspend fun updateLastRefreshTime() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_REFRESH_TIME] = System.currentTimeMillis()
        }
    }
}