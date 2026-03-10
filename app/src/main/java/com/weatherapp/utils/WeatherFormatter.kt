package com.weatherapp.utils

import com.weatherapp.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.*

object WeatherFormatter {

    fun formatTemperature(weather: Weather, unit: String): String {
        return if (unit == "C") {
            "${weather.current.tempC.toInt()}°C"
        } else {
            "${weather.current.tempF.toInt()}°F"
        }
    }

    fun formatFeelsLike(weather: Weather, unit: String): String {
        return if (unit == "C") {
            "${weather.current.feelslikeC.toInt()}°C"
        } else {
            "${weather.current.feelslikeF.toInt()}°F"
        }
    }

    fun formatWindSpeed(weather: Weather, unit: String): String {
        return if (unit == "C") {
            "${weather.current.windKph.toInt()} km/h"
        } else {
            "${weather.current.windMph.toInt()} mph"
        }
    }

    fun formatWindDirection(weather: Weather): String {
        return "${weather.current.windDir} (${weather.current.windDegree}°)"
    }

    fun formatVisibility(weather: Weather): String {
        return when {
            weather.current.visibilityKm < 1 -> "Poor (<1 km)"
            weather.current.visibilityKm < 4 -> "Moderate (1-4 km)"
            weather.current.visibilityKm < 10 -> "Good (4-10 km)"
            else -> "Excellent (>10 km)"
        }
    }

    fun formatUVIndex(uv: Double): String {
        return when {
            uv <= 2 -> "Low"
            uv <= 5 -> "Moderate"
            uv <= 7 -> "High"
            uv <= 10 -> "Very High"
            else -> "Extreme"
        }
    }

    fun formatLastUpdated(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault())
        return format.format(date)
    }

    fun getAirQualityDescription(index: Int): String {
        return when (index) {
            1 -> "Good"
            2 -> "Moderate"
            3 -> "Unhealthy for Sensitive Groups"
            4 -> "Unhealthy"
            5 -> "Very Unhealthy"
            6 -> "Hazardous"
            else -> "Unknown"
        }
    }

    fun getAirQualityColor(index: Int): Pair<Int, Int> {
        return when (index) {
            1 -> Pair(0xFF4CAF50.toInt(), 0xFF81C784.toInt()) // Green
            2 -> Pair(0xFFFFC107.toInt(), 0xFFFFD54F.toInt()) // Yellow
            3 -> Pair(0xFFFF9800.toInt(), 0xFFFFB74D.toInt()) // Orange
            4 -> Pair(0xFFF44336.toInt(), 0xFFE57373.toInt()) // Red
            5 -> Pair(0xFF9C27B0.toInt(), 0xFFBA68C8.toInt()) // Purple
            6 -> Pair(0xFF7B1FA2.toInt(), 0xFF9C27B0.toInt()) // Dark Purple
            else -> Pair(0xFF9E9E9E.toInt(), 0xFFBDBDBD.toInt()) // Grey
        }
    }
}