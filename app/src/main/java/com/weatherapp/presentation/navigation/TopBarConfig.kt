package com.weatherapp.presentation.navigation

data class TopBarConfig(
    val title: String,
    val showBack: Boolean = false,
    val showSearch: Boolean = false,
    val showSettings: Boolean = false
)

fun getTopBarConfig(route: String?): TopBarConfig {
    return when (route) {
        NavRoutes.Home.route -> TopBarConfig(
            title = "Weather",
            showSearch = true,
            showSettings = true
        )
        NavRoutes.Search.route -> TopBarConfig(
            title = "Search City",
            showBack = true
        )
        NavRoutes.SavedCities.route -> TopBarConfig(
            title = "Saved Cities",
            showBack = true,
            showSettings = true
        )
        NavRoutes.Settings.route -> TopBarConfig(
            title = "Settings",
            showBack = true
        )
        else -> TopBarConfig(
            title = "Weather App",
            showSettings = true
        )
    }
}