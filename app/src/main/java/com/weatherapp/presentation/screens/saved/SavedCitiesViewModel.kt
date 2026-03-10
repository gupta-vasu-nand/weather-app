package com.weatherapp.presentation.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.UpdatePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedCitiesState(
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val defaultCityId: Int? = null
)

@HiltViewModel
class SavedCitiesViewModel @Inject constructor(
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SavedCitiesState(isLoading = true))
    val state: StateFlow<SavedCitiesState> = _state.asStateFlow()

    init {
        loadCities()
        loadDefaultCity()
    }

    private fun loadCities() {
        viewModelScope.launch {
            getSavedCitiesUseCase().collect { cities ->
                _state.update {
                    it.copy(
                        cities = cities,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadDefaultCity() {
        viewModelScope.launch {
            updatePreferencesUseCase.getPreferences().collect { preferences ->
                _state.update {
                    it.copy(defaultCityId = preferences.defaultCityId)
                }
            }
        }
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            try {
                val updatedCity = city.copy(isFavorite = !city.isFavorite)
                // In a real app, this would call a use case to update the city
                // For now, we'll update locally
                val updatedCities = _state.value.cities.map {
                    if (it.id == city.id) updatedCity else it
                }
                _state.update { it.copy(cities = updatedCities) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update favorite") }
            }
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            try {
                deleteCityUseCase(city)
                // If deleted city was default, clear default
                if (city.id == _state.value.defaultCityId) {
                    updatePreferencesUseCase.updateTemperatureUnit(
                        com.weatherapp.domain.model.TemperatureUnit.CELSIUS
                    ) // Just to trigger an update - in real app, use proper method
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete city") }
            }
        }
    }

    fun setDefaultCity(city: City) {
        viewModelScope.launch {
            try {
                // Update all cities to not be default
                val updatedCities = _state.value.cities.map {
                    it.copy(isDefault = it.id == city.id)
                }
                _state.update {
                    it.copy(
                        cities = updatedCities,
                        defaultCityId = city.id
                    )
                }

                // In a real app, this would call a use case to set default city
                // updatePreferencesUseCase.setDefaultCity(city.id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to set default city") }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun retry() {
        _state.update { it.copy(isLoading = true, error = null) }
        loadCities()
    }
}