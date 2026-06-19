package com.weatherapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Weather(
    val location: Location,
    val current: CurrentWeather
)

@Immutable
data class CurrentWeather(
    val lastUpdated: String,
    val tempC: Double,
    val tempF: Double,
    val isDay: Boolean,
    val condition: WeatherCondition,
    val windMph: Double,
    val windKph: Double,
    val windDegree: Int,
    val windDir: String,
    val pressureMb: Double,
    val pressureIn: Double,
    val precipMm: Double,
    val precipIn: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslikeC: Double,
    val feelslikeF: Double,
    val heatindexC: Double? = null,
    val heatindexF: Double? = null,
    val dewpointC: Double? = null,
    val dewpointF: Double? = null,
    val visibilityKm: Double,
    val visibilityMiles: Double,
    val uv: Double,
    val gustMph: Double,
    val gustKph: Double,
    val airQuality: AirQuality?
)

@Immutable
data class WeatherCondition(
    val text: String,
    val icon: String,
    val code: Int
)