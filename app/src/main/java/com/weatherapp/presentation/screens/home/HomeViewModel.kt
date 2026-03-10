package com.weatherapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
import com.weatherapp.domain.usecase.UpdatePreferencesUseCase
import com.weatherapp.utils.NetworkMonitor
import com.weatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeData()
        observeNetworkStatus()
        observePreferences()

        // Auto refresh every 30 minutes
        viewModelScope.launch {
            while (true) {
                delay(30 * 60 * 1000L) // 30 minutes
                refreshWeather()
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getSavedCitiesUseCase(),
                updatePreferencesUseCase.getPreferences()
            ) { cities, preferences ->
                val defaultCity = cities.find { it.id == preferences.defaultCityId }
                val selectedCity = defaultCity?.cityName ?: cities.firstOrNull()?.cityName ?: "Delhi"
                val selectedCityId = defaultCity?.id ?: cities.firstOrNull()?.id

                _state.update {
                    it.copy(
                        savedCities = cities,
                        selectedCity = selectedCity,
                        selectedCityId = selectedCityId
                    )
                }

                // Load weather for the selected city
                if (cities.isNotEmpty()) {
                    loadWeather(selectedCity)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _state.update { it.copy(isOnline = isOnline) }
                if (isOnline && _state.value.weather == null) {
                    loadWeather(_state.value.selectedCity)
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            updatePreferencesUseCase.getPreferences().collect { preferences ->
                _state.update {
                    it.copy(
                        temperatureUnit = if (preferences.temperatureUnit == TemperatureUnit.CELSIUS) "C" else "F"
                    )
                }
            }
        }
    }

    fun loadWeather(city: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getCurrentWeatherUseCase(city)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            weather = result.data,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            loadWeather(_state.value.selectedCity)
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun selectCity(city: City) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCity = city.cityName,
                    selectedCityId = city.id,
                    showCitySelector = false
                )
            }
            loadWeather(city.cityName)
        }
    }

    fun toggleCitySelector() {
        _state.update { it.copy(showCitySelector = !it.showCitySelector) }
    }

    fun retry() {
        loadWeather(_state.value.selectedCity)
    }

    fun updateTemperatureUnit(unit: String) {
        viewModelScope.launch {
            val temperatureUnit = if (unit == "C") TemperatureUnit.CELSIUS else TemperatureUnit.FAHRENHEIT
            updatePreferencesUseCase.updateTemperatureUnit(temperatureUnit)
        }
    }
}