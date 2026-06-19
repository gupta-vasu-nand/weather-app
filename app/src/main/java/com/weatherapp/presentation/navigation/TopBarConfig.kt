package com.weatherapp.presentation.navigation

data class TopBarConfig(
    val title: String,
    val showBack: Boolean = false,
    val showSearch: Boolean = false,
    val showSettings: Boolean = false,
    val showSavedCities: Boolean = false,
    val isLarge: Boolean = false
)

fun getTopBarConfig(route: String?): TopBarConfig {
    return when (route) {
        NavRoutes.Home.route -> TopBarConfig(
            title = NavRoutes.Home.title,
            showSearch = true,
            isLarge = true
        )
        NavRoutes.Search.route -> TopBarConfig(
            title = NavRoutes.Search.title,
            showBack = true
        )
        NavRoutes.SavedCities.route -> TopBarConfig(
            title = NavRoutes.SavedCities.title,
            showSearch = true
        )
        NavRoutes.Settings.route -> TopBarConfig(
            title = NavRoutes.Settings.title
        )
        else -> TopBarConfig(
            title = "Weather App",
            showSettings = true
        )
    }
}