package com.weatherapp.data.local.db.dao

import androidx.room.*
import com.weatherapp.data.local.db.entity.PreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {

    @Query("SELECT * FROM user_preferences LIMIT 1")
    fun getPreferences(): Flow<PreferencesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: PreferencesEntity)

    @Update
    suspend fun updatePreferences(preferences: PreferencesEntity)

    @Query("UPDATE user_preferences SET temperatureUnit = :unit")
    suspend fun updateTemperatureUnit(unit: String)

    @Query("UPDATE user_preferences SET themeMode = :mode")
    suspend fun updateThemeMode(mode: String)

    @Query("UPDATE user_preferences SET notificationsEnabled = :enabled")
    suspend fun toggleNotifications(enabled: Boolean)

    @Query("UPDATE user_preferences SET defaultCityId = :cityId")
    suspend fun updateDefaultCity(cityId: Int?)
}