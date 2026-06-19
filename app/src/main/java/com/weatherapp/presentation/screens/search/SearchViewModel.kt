package com.weatherapp.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityType
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
import com.weatherapp.domain.usecase.SaveCityUseCase
import com.weatherapp.domain.usecase.DeleteCityUseCase
import com.weatherapp.domain.usecase.SearchCityUseCase
import com.weatherapp.utils.LocationTracker
import com.weatherapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val searchQuery: String = "",
    val searchResults: List<City> = emptyList(),
    val recentSearches: List<City> = emptyList(),
    val isSearching: Boolean = false,
    val isLocating: Boolean = false,
    val error: String? = null,
    val selectedCityType: CityType = CityType.OTHER,
    val successMessage: String? = null,
    val shouldNavigateBack: Boolean = false,
    val customCityInput: String = ""
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val searchCityUseCase: SearchCityUseCase,
    private val updatePreferencesUseCase: com.weatherapp.domain.usecase.UpdatePreferencesUseCase,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            getSavedCitiesUseCase().collect { cities ->
                _state.update {
                    it.copy(
                        recentSearches = cities.take(10)
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                error = null,
                successMessage = null
            )
        }

        if (query.length >= 2) {
            searchCity(query)
        } else {
            _state.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun searchCity(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true, error = null) }
            
            when (val result = searchCityUseCase(query)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isSearching = false,
                            searchResults = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isSearching = false,
                            error = result.message
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isSearching = true) }
                }
            }
        }
    }

    fun detectLocationAndSearch() {
        viewModelScope.launch {
            _state.update { it.copy(isLocating = true, error = null) }
            val location = locationTracker.getCurrentLocation()
            _state.update { it.copy(isLocating = false) }

            if (location != null) {
                val query = "${location.latitude},${location.longitude}"
                searchCity(query)
                _state.update { it.copy(searchQuery = "Current Location") }
            } else {
                _state.update { it.copy(error = "Could not detect location. Make sure GPS is on.") }
            }
        }
    }

    fun saveCity(city: City) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isSearching = true) }
                
                // Set the type user selected
                val cityToSave = city.copy(
                    type = _state.value.selectedCityType,
                    id = 0,
                    isFavorite = false
                )

                val savedId = saveCityUseCase(cityToSave)
                
                if (savedId > 0) {
                    updatePreferencesUseCase.setDefaultCity(savedId.toInt())
                    _state.update {
                        it.copy(
                            isSearching = false,
                            successMessage = "City saved",
                            shouldNavigateBack = true
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(isSearching = false, error = "Failed to save city") 
                }
            }
        }
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            saveCityUseCase(city.copy(isFavorite = !city.isFavorite))
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            deleteCityUseCase(city)
        }
    }

    fun selectRecentCity(city: City) {
        viewModelScope.launch {
            updatePreferencesUseCase.setDefaultCity(city.id)
            _state.update { it.copy(shouldNavigateBack = true) }
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                error = null
            )
        }
    }

    fun setCityType(type: CityType) {
        _state.update { it.copy(selectedCityType = type) }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }

    fun resetNavigation() {
        _state.update { it.copy(shouldNavigateBack = false) }
    }
}
