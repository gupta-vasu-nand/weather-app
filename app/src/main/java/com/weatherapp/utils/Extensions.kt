package com.weatherapp.utils

import androidx.compose.ui.graphics.Color

fun getWeatherGradient(conditionCode: Int, isDay: Boolean): List<Color> {
    return when {
        Constants.SUNNY_CODES.contains(conditionCode) -> {
            if (isDay) {
                listOf(Color(0xFFFFB74D), Color(0xFFFF8A65))
            } else {
                listOf(Color(0xFF303F9F), Color(0xFF283593))
            }
        }
        Constants.PARTLY_CLOUDY_CODES.contains(conditionCode) -> {
            if (isDay) {
                listOf(Color(0xFF90A4AE), Color(0xFF607D8B))
            } else {
                listOf(Color(0xFF455A64), Color(0xFF37474F))
            }
        }
        Constants.CLOUDY_CODES.contains(conditionCode) -> {
            listOf(Color(0xFF78909C), Color(0xFF546E7A))
        }
        Constants.RAINY_CODES.contains(conditionCode) -> {
            listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))
        }
        Constants.SNOWY_CODES.contains(conditionCode) -> {
            listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC))
        }
        Constants.STORMY_CODES.contains(conditionCode) -> {
            listOf(Color(0xFF5C6BC0), Color(0xFF283593))
        }
        else -> {
            if (isDay) {
                listOf(Color(0xFF90A4AE), Color(0xFF607D8B))
            } else {
                listOf(Color(0xFF455A64), Color(0xFF37474F))
            }
        }
    }
}

fun getWeatherIcon(conditionCode: Int, isDay: Boolean): Int {
    // Return appropriate icon resource ID based on condition code
    // This is a placeholder - implement based on your icon set
    return android.R.drawable.ic_menu_compass // Replace with actual icons
}