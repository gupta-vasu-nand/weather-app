package com.weatherapp.data.local.db.dao

import androidx.room.*
import com.weatherapp.data.local.db.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE cityName = :cityName")
    suspend fun getByCityName(cityName: String): WeatherCacheEntity?

    @Query("SELECT * FROM weather_cache ORDER BY timestamp DESC LIMIT 1")
    fun getLastWeather(): Flow<WeatherCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE cityName = :cityName")
    suspend fun deleteByCityName(cityName: String)

    @Query("DELETE FROM weather_cache")
    suspend fun clearAll()

    @Query("DELETE FROM weather_cache WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}