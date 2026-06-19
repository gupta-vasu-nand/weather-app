package com.weatherapp.domain.repository

import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.model.UserPreferences
import com.weatherapp.domain.model.Weather
import com.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    // Weather data
    suspend fun getCurrentWeather(city: String): Resource<Weather>
    suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<Weather>
    suspend fun searchCities(query: String): Resource<List<City>>
    fun getLastWeather(): Flow<Weather?>

    // City management
    fun getAllCities(): Flow<List<City>>
    suspend fun saveCity(city: City): Long
    suspend fun deleteCity(city: City)
    suspend fun updateCity(city: City)
    suspend fun setDefaultCity(cityId: Int)
    fun getDefaultCity(): Flow<City?>

    // Preferences
    fun getUserPreferences(): Flow<UserPreferences>
    suspend fun updateUserPreferences(preferences: UserPreferences)
    suspend fun updateTemperatureUnit(unit: TemperatureUnit)
    suspend fun updateWindSpeedUnit(unit: com.weatherapp.domain.model.WindSpeedUnit)
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun toggleNotifications(enabled: Boolean)

    // Offline support
    suspend fun cacheWeather(weather: Weather)
    suspend fun clearCache()
}