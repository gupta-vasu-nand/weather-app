package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.model.UserPreferences
import com.weatherapp.domain.model.WindSpeedUnit
import com.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdatePreferencesUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    fun getPreferences(): Flow<UserPreferences> {
        return repository.getUserPreferences()
    }

    suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        repository.updateTemperatureUnit(unit)
    }

    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        repository.updateWindSpeedUnit(unit)
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        repository.updateThemeMode(mode)
    }

    suspend fun toggleNotifications(enabled: Boolean) {
        repository.toggleNotifications(enabled)
    }

    suspend fun setDefaultCity(cityId: Int) {
        repository.setDefaultCity(cityId)
    }
}