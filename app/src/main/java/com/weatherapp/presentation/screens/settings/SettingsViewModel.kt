package com.weatherapp.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.domain.model.TemperatureUnit
import com.weatherapp.domain.model.ThemeMode
import com.weatherapp.domain.model.WindSpeedUnit
import com.weatherapp.domain.usecase.UpdatePreferencesUseCase
import com.weatherapp.utils.WeatherCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KPH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val cacheSize: Long = 0L,
    val isClearingCache: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updatePreferencesUseCase: UpdatePreferencesUseCase,
    private val cacheManager: WeatherCacheManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(isLoading = true))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadPreferences()
        loadCacheSize()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            updatePreferencesUseCase.getPreferences().collect { preferences ->
                _state.update {
                    it.copy(
                        temperatureUnit = preferences.temperatureUnit,
                        windSpeedUnit = preferences.windSpeedUnit,
                        themeMode = preferences.themeMode,
                        notificationsEnabled = preferences.notificationsEnabled,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadCacheSize() {
        viewModelScope.launch {
            val size = cacheManager.getCacheSize()
            _state.update { it.copy(cacheSize = size) }
        }
    }

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(temperatureUnit = unit) }
                updatePreferencesUseCase.updateTemperatureUnit(unit)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update temperature unit") }
            }
        }
    }

    fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(windSpeedUnit = unit) }
                updatePreferencesUseCase.updateWindSpeedUnit(unit)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update wind speed unit") }
            }
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(themeMode = mode) }
                updatePreferencesUseCase.updateThemeMode(mode)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update theme") }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(notificationsEnabled = enabled) }
                updatePreferencesUseCase.toggleNotifications(enabled)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to update notifications") }
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isClearingCache = true) }
                cacheManager.clearCache()
                val newSize = cacheManager.getCacheSize()
                _state.update {
                    it.copy(
                        isClearingCache = false,
                        cacheSize = newSize
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isClearingCache = false,
                        error = "Failed to clear cache: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                updateTemperatureUnit(TemperatureUnit.CELSIUS)
                updateWindSpeedUnit(WindSpeedUnit.KPH)
                updateThemeMode(ThemeMode.SYSTEM)
                toggleNotifications(true)

                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to reset settings"
                    )
                }
            }
        }
    }
}