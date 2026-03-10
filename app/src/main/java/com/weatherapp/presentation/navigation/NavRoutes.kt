package com.weatherapp.presentation.navigation

sealed class NavRoutes(
    val route: String,
    val title: String
) {
    data object Home : NavRoutes("home", "Weather")
    data object Search : NavRoutes("search", "Search City")
    data object SavedCities : NavRoutes("saved_cities", "Saved Cities")
    data object Settings : NavRoutes("settings", "Settings")

    companion object {
        val allRoutes = listOf(Home, Search, SavedCities, Settings)
    }
}