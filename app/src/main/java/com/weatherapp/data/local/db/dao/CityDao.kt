package com.weatherapp.data.local.db.dao

import androidx.room.*
import com.weatherapp.data.local.db.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM cities ORDER BY isFavorite DESC, cityName ASC")
    fun getAllCities(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE id = :cityId")
    fun getCityById(cityId: Int): Flow<CityEntity?>

    @Query("SELECT * FROM cities WHERE isDefault = 1 LIMIT 1")
    fun getDefaultCity(): Flow<CityEntity?>

    @Insert
    suspend fun insertCity(city: CityEntity): Long

    @Update
    suspend fun updateCity(city: CityEntity)

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Query("UPDATE cities SET isDefault = 0")
    suspend fun clearDefaultCity()

    @Query("UPDATE cities SET isDefault = 1 WHERE id = :cityId")
    suspend fun setDefaultCity(cityId: Int)

    @Query("UPDATE cities SET isFavorite = :isFavorite WHERE id = :cityId")
    suspend fun updateFavoriteStatus(cityId: Int, isFavorite: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM cities WHERE cityName = :cityName)")
    suspend fun isCitySaved(cityName: String): Boolean
}