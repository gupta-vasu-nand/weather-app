package com.weatherapp.data.local.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.weatherapp.data.local.db.converters.Converters
import com.weatherapp.data.local.db.dao.CityDao
import com.weatherapp.data.local.db.dao.PreferencesDao
import com.weatherapp.data.local.db.dao.WeatherCacheDao
import com.weatherapp.data.local.db.entity.CityEntity
import com.weatherapp.data.local.db.entity.PreferencesEntity
import com.weatherapp.data.local.db.entity.WeatherCacheEntity

@Database(
    entities = [
        CityEntity::class,
        PreferencesEntity::class,
        WeatherCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}