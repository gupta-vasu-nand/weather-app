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
    private val saveCityUseCase: com.weatherapp.domain.usecase.SaveCityUseCase,
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
                saveCityUseCase(city.copy(isFavorite = !city.isFavorite))
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update favorite") }
            }
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            try {
                deleteCityUseCase(city)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete city") }
            }
        }
    }

    fun setDefaultCity(city: City) {
        viewModelScope.launch {
            try {
                updatePreferencesUseCase.setDefaultCity(city.id)
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