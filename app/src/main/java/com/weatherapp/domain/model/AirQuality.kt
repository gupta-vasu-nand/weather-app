package com.weatherapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AirQuality(
    val co: Double,
    val no2: Double,
    val o3: Double,
    val so2: Double,
    val pm2_5: Double,
    val pm10: Double,
    val usEpaIndex: Int,
    val gbDefraIndex: Int
) {
    fun getAirQualityLevel(): AirQualityLevel {
        return when (usEpaIndex) {
            1 -> AirQualityLevel.GOOD
            2 -> AirQualityLevel.MODERATE
            3 -> AirQualityLevel.UNHEALTHY_SENSITIVE
            4 -> AirQualityLevel.UNHEALTHY
            5 -> AirQualityLevel.VERY_UNHEALTHY
            else -> AirQualityLevel.HAZARDOUS
        }
    }
}

enum class AirQualityLevel {
    GOOD,
    MODERATE,
    UNHEALTHY_SENSITIVE,
    UNHEALTHY,
    VERY_UNHEALTHY,
    HAZARDOUS
}