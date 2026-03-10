package com.weatherapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityType

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val type: String,
    val isFavorite: Boolean = false,
    val isDefault: Boolean = false
) {
    fun toDomain(): City {
        return City(
            id = id,
            cityName = cityName,
            lat = lat,
            lon = lon,
            type = CityType.valueOf(type),
            isFavorite = isFavorite,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromDomain(city: City): CityEntity {
            return CityEntity(
                id = city.id,
                cityName = city.cityName,
                lat = city.lat,
                lon = city.lon,
                type = city.type.name,
                isFavorite = city.isFavorite,
                isDefault = city.isDefault
            )
        }
    }
}