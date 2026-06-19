package com.weatherapp.utils

import androidx.compose.ui.graphics.Color

fun getWeatherGradient(conditionCode: Int, isDay: Boolean): List<Color> {
    return when {
        Constants.SUNNY_CODES.contains(conditionCode) -> {
            if (isDay) {
                // Vibrant Summer Day
                listOf(
                    Color(0xFFFFD54F), // Amber 300
                    Color(0xFFFFB74D), // Orange 300
                    Color(0xFFFF8A65)  // Deep Orange 300
                )
            } else {
                // Clear Starry Night
                listOf(
                    Color(0xFF1A237E), // Indigo 900
                    Color(0xFF283593), // Indigo 800
                    Color(0xFF311B92)  // Deep Purple 900
                )
            }
        }
        Constants.PARTLY_CLOUDY_CODES.contains(conditionCode) -> {
            if (isDay) {
                // Soft Afternoon Cloud
                listOf(
                    Color(0xFF81D4FA), // Light Blue 200
                    Color(0xFF4FC3F7), // Light Blue 300
                    Color(0xFF90A4AE)  // Blue Grey 300
                )
            } else {
                // Dim Moonlight
                listOf(
                    Color(0xFF37474F), // Blue Grey 800
                    Color(0xFF263238), // Blue Grey 900
                    Color(0xFF102027)  // Dark Space
                )
            }
        }
        Constants.CLOUDY_CODES.contains(conditionCode) -> {
            if (isDay) {
                // Overcast Day
                listOf(
                    Color(0xFFB0BEC5), // Blue Grey 200
                    Color(0xFF90A4AE), // Blue Grey 300
                    Color(0xFF78909C)  // Blue Grey 400
                )
            } else {
                // Moody Overcast Night
                listOf(
                    Color(0xFF455A64), // Blue Grey 700
                    Color(0xFF37474F), // Blue Grey 800
                    Color(0xFF263238)  // Blue Grey 900
                )
            }
        }
        Constants.RAINY_CODES.contains(conditionCode) -> {
            // Cool Refreshing Rain
            listOf(
                Color(0xFF4DD0E1), // Cyan 300
                Color(0xFF0288D1), // Light Blue 700
                Color(0xFF01579B)  // Light Blue 900
            )
        }
        Constants.SNOWY_CODES.contains(conditionCode) -> {
            // Crisp Arctic Snow
            listOf(
                Color(0xFFE1F5FE), // Light Blue 50
                Color(0xFFB3E5FC), // Light Blue 100
                Color(0xFFE0F7FA)  // Cyan 50
            )
        }
        Constants.STORMY_CODES.contains(conditionCode) -> {
            // Intense Electric Storm
            listOf(
                Color(0xFF5C6BC0), // Indigo 400
                Color(0xFF3949AB), // Indigo 600
                Color(0xFF1A237E)  // Indigo 900
            )
        }
        else -> {
            if (isDay) {
                listOf(Color(0xFF81D4FA), Color(0xFF4FC3F7), Color(0xFF90A4AE))
            } else {
                listOf(Color(0xFF37474F), Color(0xFF263238), Color(0xFF102027))
            }
        }
    }
}

fun getWeatherIcon(conditionCode: Int, isDay: Boolean): Int {
    // Return appropriate icon resource ID based on condition code
    // This is a placeholder - implement based on your icon set
    return android.R.drawable.ic_menu_compass // Replace with actual icons
}
