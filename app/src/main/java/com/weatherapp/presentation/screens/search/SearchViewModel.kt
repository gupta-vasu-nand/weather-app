package com.weatherapp.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.CityType
import com.weatherapp.domain.usecase.GetSavedCitiesUseCase
import com.weatherapp.domain.usecase.SaveCityUseCase
import com.weatherapp.domain.usecase.DeleteCityUseCase
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
    private val deleteCityUseCase: DeleteCityUseCase
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
                        recentSearches = cities.take(5)
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
                successMessage = null,
                customCityInput = if (query.length >= 3) query else ""
            )
        }

        // Debounce search to avoid too many API calls
        viewModelScope.launch {
            delay(500)
            if (query.isNotBlank() && query.length >= 3) {
                searchCity(query)
            } else {
                _state.update { it.copy(searchResults = emptyList()) }
            }
        }
    }

    fun searchCity(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true, error = null) }

            // Simulate network delay
            delay(1000)

            // In a real app, this would call a search API
            val mockResults = generateMockSearchResults(query)

            _state.update {
                it.copy(
                    isSearching = false,
                    searchResults = mockResults
                )
            }
        }
    }

    fun addCustomCity() {
        viewModelScope.launch {
            val customCityName = _state.value.searchQuery.trim()
            if (customCityName.length < 3) {
                _state.update {
                    it.copy(error = "City name must be at least 3 characters")
                }
                return@launch
            }

            // Check if city already exists in saved cities
            val existingCities = _state.value.recentSearches
            val cityExists = existingCities.any {
                it.cityName.equals(customCityName, ignoreCase = true)
            }

            if (cityExists) {
                _state.update {
                    it.copy(error = "City '$customCityName' is already saved")
                }
                return@launch
            }

            // Create a custom city with approximate coordinates (in production, geocoding API would be used)
            val customCity = City(
                id = 0,
                cityName = customCityName,
                lat = 0.0, // Placeholder - in production, use geocoding
                lon = 0.0, // Placeholder - in production, use geocoding
                type = _state.value.selectedCityType,
                isFavorite = false
            )

            saveCityInternal(customCity)
        }
    }

    fun saveCity(city: City) {
        viewModelScope.launch {
            saveCityInternal(city)
        }
    }

    private suspend fun saveCityInternal(city: City) {
        try {
            // Check if city already exists in saved cities
            val existingCities = _state.value.recentSearches
            val cityExists = existingCities.any {
                it.cityName.equals(city.cityName, ignoreCase = true)
            }

            if (cityExists) {
                _state.update {
                    it.copy(error = "City '${city.cityName}' is already saved")
                }
                return
            }

            // Create a new city with the selected type
            val cityToSave = city.copy(
                type = _state.value.selectedCityType,
                id = 0, // Let Room auto-generate the ID
                isFavorite = false
            )

            println("DEBUG: Attempting to save city: ${cityToSave.cityName}")
            val savedId = saveCityUseCase(cityToSave)
            println("DEBUG: City saved with ID: $savedId")

            if (savedId > 0) {
                _state.update {
                    it.copy(
                        successMessage = "City '${city.cityName}' saved successfully",
                        searchQuery = "",
                        searchResults = emptyList(),
                        customCityInput = "",
                        shouldNavigateBack = true // Trigger navigation
                    )
                }
                // Refresh recent searches
                loadRecentSearches()

                // Reset navigation flag after a delay
                delay(100)
                _state.update { it.copy(shouldNavigateBack = false) }
            } else {
                _state.update {
                    it.copy(error = "Failed to save city: Database error")
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Error saving city: ${e.message}")
            e.printStackTrace()
            _state.update {
                it.copy(error = "Failed to save city: ${e.message}")
            }
        }
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            try {
                val updatedCity = city.copy(isFavorite = !city.isFavorite)
                saveCityUseCase(updatedCity)
                loadRecentSearches()
                _state.update {
                    it.copy(successMessage = "Favorite updated")
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update favorite") }
            }
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch {
            try {
                deleteCityUseCase(city)
                loadRecentSearches()
                _state.update {
                    it.copy(successMessage = "City deleted")
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete city") }
            }
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                error = null,
                successMessage = null,
                customCityInput = ""
            )
        }
    }

    fun setCityType(type: CityType) {
        _state.update { it.copy(selectedCityType = type) }
    }

    fun clearMessages() {
        _state.update {
            it.copy(error = null, successMessage = null)
        }
    }

    fun resetNavigation() {
        _state.update { it.copy(shouldNavigateBack = false) }
    }

    private fun generateMockSearchResults(query: String): List<City> {
        // Predefined list of major cities
        val mockCities = listOf(
            City(cityName = "New York", lat = 40.7128, lon = -74.0060, type = CityType.OTHER),
            City(cityName = "Los Angeles", lat = 34.0522, lon = -118.2437, type = CityType.OTHER),
            City(cityName = "Chicago", lat = 41.8781, lon = -87.6298, type = CityType.OTHER),
            City(cityName = "Houston", lat = 29.7604, lon = -95.3698, type = CityType.OTHER),
            City(cityName = "Phoenix", lat = 33.4484, lon = -112.0740, type = CityType.OTHER),
            City(cityName = "Philadelphia", lat = 39.9526, lon = -75.1652, type = CityType.OTHER),
            City(cityName = "San Antonio", lat = 29.4241, lon = -98.4936, type = CityType.OTHER),
            City(cityName = "San Diego", lat = 32.7157, lon = -117.1611, type = CityType.OTHER),
            City(cityName = "Dallas", lat = 32.7767, lon = -96.7970, type = CityType.OTHER),
            City(cityName = "San Jose", lat = 37.3382, lon = -121.8863, type = CityType.OTHER),
            City(cityName = "London", lat = 51.5074, lon = -0.1278, type = CityType.OTHER),
            City(cityName = "Tokyo", lat = 35.6762, lon = 139.6503, type = CityType.OTHER),
            City(cityName = "Paris", lat = 48.8566, lon = 2.3522, type = CityType.OTHER),
            City(cityName = "Sydney", lat = -33.8688, lon = 151.2093, type = CityType.OTHER),
            City(cityName = "Delhi", lat = 28.6139, lon = 77.2090, type = CityType.OTHER),
            City(cityName = "Mumbai", lat = 19.0760, lon = 72.8777, type = CityType.OTHER),
            City(cityName = "Bangalore", lat = 12.9716, lon = 77.5946, type = CityType.OTHER),
            City(cityName = "Chennai", lat = 13.0827, lon = 80.2707, type = CityType.OTHER),
            City(cityName = "Kolkata", lat = 22.5726, lon = 88.3639, type = CityType.OTHER),
            City(cityName = "Hyderabad", lat = 17.3850, lon = 78.4867, type = CityType.OTHER),
            City(cityName = "Pune", lat = 18.5204, lon = 73.8567, type = CityType.OTHER),
            City(cityName = "Ahmedabad", lat = 23.0225, lon = 72.5714, type = CityType.OTHER),
            City(cityName = "Jaipur", lat = 26.9124, lon = 75.7873, type = CityType.OTHER),
            City(cityName = "Lucknow", lat = 26.8467, lon = 80.9462, type = CityType.OTHER)
        )

        val results = mockCities
            .filter { it.cityName.contains(query, ignoreCase = true) }
            .mapIndexed { index, city ->
                city.copy(
                    id = index + 1000,
                    type = CityType.OTHER
                )
            }

        return results
    }
}