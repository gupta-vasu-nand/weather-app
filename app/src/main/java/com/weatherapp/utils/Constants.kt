package com.weatherapp.utils

object Constants {
    const val DATABASE_NAME = "weather_app_database"
    const val PREFERENCES_NAME = "weather_prefs"
    const val WEATHER_CACHE_TIMEOUT = 30 * 60 * 1000L // 30 minutes

    // API Constants
    const val API_BASE_URL = "https://api.weatherapi.com/v1/"
    const val API_TIMEOUT_SECONDS = 30L

    // Weather condition codes for gradients
    val SUNNY_CODES = listOf(1000)
    val PARTLY_CLOUDY_CODES = listOf(1003)
    val CLOUDY_CODES = listOf(1006, 1009)
    val RAINY_CODES = listOf(1063, 1180, 1183, 1186, 1189, 1192, 1195, 1240, 1243, 1246)
    val SNOWY_CODES = listOf(1066, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1255, 1258)
    val STORMY_CODES = listOf(1087, 1273, 1276, 1279, 1282)
}