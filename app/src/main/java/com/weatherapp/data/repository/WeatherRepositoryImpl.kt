package com.weatherapp.data.repository

import com.weatherapp.data.local.db.AppDatabase
import com.weatherapp.data.local.db.entity.CityEntity
import com.weatherapp.data.local.db.entity.PreferencesEntity
import com.weatherapp.data.local.db.entity.WeatherCacheEntity
import com.weatherapp.data.local.datastore.SettingsDataStore
import com.weatherapp.data.remote.source.RemoteDataSource
import com.weatherapp.domain.model.*
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.weatherapp.data.remote.dto.toDomain
import kotlinx.coroutines.flow.emitAll
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val remoteDataSource: RemoteDataSource,
    private val settingsDataStore: SettingsDataStore,
    private val gson: Gson
) : WeatherRepository {

    // Weather data
    override suspend fun getCurrentWeather(city: String): Resource<Weather> = withContext(Dispatchers.IO) {
        when (val result = remoteDataSource.getCurrentWeather(city)) {
            is Resource.Success -> {
                val weather = result.data.toDomain()
                cacheWeather(weather)
                Resource.Success(weather)
            }
            is Resource.Error -> {
                // Try to get cached data
                val cachedWeather = getCachedWeather(city)
                if (cachedWeather != null) {
                    Resource.Success(cachedWeather)
                } else {
                    Resource.Error(result.message, result.code, result.isNetworkError)
                }
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<Weather> = withContext(Dispatchers.IO) {
        when (val result = remoteDataSource.getCurrentWeather(lat, lon)) {
            is Resource.Success -> {
                val weather = result.data.toDomain()
                cacheWeather(weather)
                Resource.Success(weather)
            }
            is Resource.Error -> {
                // Try to get cached data for nearest city
                val cachedWeather = getCachedWeather("$lat,$lon")
                if (cachedWeather != null) {
                    Resource.Success(cachedWeather)
                } else {
                    Resource.Error(result.message, result.code, result.isNetworkError)
                }
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override fun getLastWeather(): Flow<Weather?> = flow {
        val cache = database.cityDao().getDefaultCity().map { cityEntity ->
            cityEntity?.let {
                // In production, properly map from cache
                null
            }
        }
        emitAll(cache)
    }

    // City management
    override fun getAllCities(): Flow<List<City>> {
        return database.cityDao().getAllCities().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveCity(city: City): Long {
        val entity = CityEntity.fromDomain(city)
        return database.cityDao().insertCity(entity)
    }

    override suspend fun deleteCity(city: City) {
        val entity = CityEntity.fromDomain(city)
        database.cityDao().deleteCity(entity)
    }

    override suspend fun updateCity(city: City) {
        val entity = CityEntity.fromDomain(city)
        database.cityDao().updateCity(entity)
    }

    override suspend fun setDefaultCity(cityId: Int) {
        database.cityDao().clearDefaultCity()
        database.cityDao().setDefaultCity(cityId)
        settingsDataStore.setDefaultCityId(cityId)
    }

    override fun getDefaultCity(): Flow<City?> {
        return database.cityDao().getDefaultCity().map { entity ->
            entity?.toDomain()
        }
    }

    // Preferences
    override fun getUserPreferences(): Flow<UserPreferences> {
        return combine(
            settingsDataStore.temperatureUnit,
            settingsDataStore.windSpeedUnit,
            settingsDataStore.themeMode,
            settingsDataStore.notificationsEnabled,
            settingsDataStore.defaultCityId
        ) { tempUnit, windUnit, theme, notifications, defaultCityId ->
            UserPreferences(
                temperatureUnit = tempUnit,
                windSpeedUnit = windUnit,
                themeMode = theme,
                notificationsEnabled = notifications,
                defaultCityId = defaultCityId
            )
        }
    }

    override suspend fun updateUserPreferences(preferences: UserPreferences) {
        settingsDataStore.updateTemperatureUnit(preferences.temperatureUnit)
        settingsDataStore.updateWindSpeedUnit(preferences.windSpeedUnit)
        settingsDataStore.updateThemeMode(preferences.themeMode)
        settingsDataStore.toggleNotifications(preferences.notificationsEnabled)
        settingsDataStore.setDefaultCityId(preferences.defaultCityId)

        // Also update Room for offline support
        val entity = PreferencesEntity.fromDomain(preferences)
        database.preferencesDao().insertPreferences(entity)
    }

    override suspend fun updateTemperatureUnit(unit: TemperatureUnit) {
        settingsDataStore.updateTemperatureUnit(unit)
        database.preferencesDao().updateTemperatureUnit(unit.name)
    }

    suspend fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        settingsDataStore.updateWindSpeedUnit(unit)
        // Add database update if needed
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        settingsDataStore.updateThemeMode(mode)
        database.preferencesDao().updateThemeMode(mode.name)
    }

    override suspend fun toggleNotifications(enabled: Boolean) {
        settingsDataStore.toggleNotifications(enabled)
        database.preferencesDao().toggleNotifications(enabled)
    }

    // Offline support
    override suspend fun cacheWeather(weather: Weather) {
        val cacheEntity = WeatherCacheEntity(
            cityName = weather.location.name,
            weatherData = gson.toJson(weather),
            timestamp = System.currentTimeMillis(),
            lastUpdated = weather.current.lastUpdated
        )
        // Save to database
        // database.weatherCacheDao().insert(cacheEntity)
    }

    override suspend fun clearCache() {
        // database.weatherCacheDao().clearAll()
    }

    private suspend fun getCachedWeather(city: String): Weather? {
        // Implement cache retrieval
        return null
    }
}