package com.weatherapp.presentation.screens.home

import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Weather

data class HomeState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val lastUpdated: Long = 0L,
    val selectedCity: String = "",
    val selectedCityId: Int? = null,
    val savedCities: List<City> = emptyList(),
    val temperatureUnit: String = "C",
    val isOnline: Boolean = true,
    val showCitySelector: Boolean = false
)