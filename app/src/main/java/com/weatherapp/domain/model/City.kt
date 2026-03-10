package com.weatherapp.domain.model

data class City(
    val id: Int = 0,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val type: CityType,
    val isFavorite: Boolean = false,
    val isDefault: Boolean = false
)

enum class CityType {
    HOME,
    WORK,
    OTHER
}