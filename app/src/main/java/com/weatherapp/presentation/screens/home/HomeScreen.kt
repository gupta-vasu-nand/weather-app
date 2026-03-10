package com.weatherapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.domain.model.City
import com.weatherapp.presentation.components.ErrorView
import com.weatherapp.presentation.components.LoadingOverlay
import com.weatherapp.presentation.components.WeatherCard
import com.weatherapp.presentation.components.WeatherDetailItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LoadingOverlay(isLoading = state.isLoading && !state.isRefreshing) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            // City Selector Header
            CitySelectorHeader(
                selectedCity = state.selectedCity,
                savedCities = state.savedCities,
                isExpanded = state.showCitySelector,
                onCityClick = { viewModel.toggleCitySelector() },
                onCitySelected = { viewModel.selectCity(it) },
                onSearchClick = onSearchClick
            )

            if (state.error != null && state.weather == null) {
                ErrorView(
                    message = state.error ?: "Unknown error",
                    onRetry = { viewModel.retry() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Weather Card
                    state.weather?.let { weather ->
                        item {
                            WeatherCard(
                                weather = weather,
                                temperatureUnit = state.temperatureUnit,
                                modifier = Modifier.animateItem()
                            )
                        }

                        // Last updated and network status
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Network status
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (state.isOnline) Icons.Default.Wifi else Icons.Default.SignalWifiOff,
                                        contentDescription = if (state.isOnline) "Online" else "Offline",
                                        tint = if (state.isOnline) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (state.isOnline) "Online" else "Offline",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (state.isOnline) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        }
                                    )
                                }

                                // Last updated
                                if (state.lastUpdated > 0) {
                                    Text(
                                        text = "Updated: ${android.text.format.DateFormat.format(
                                            "hh:mm a",
                                            state.lastUpdated
                                        )}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }

                        // Weather Details Grid
                        item {
                            WeatherDetailsContent(weather = weather, temperatureUnit = state.temperatureUnit)
                        }
                    }
                }
            }
        }
    }

    // Pull to refresh
    PullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.refreshWeather() }
    )
}

@Composable
fun CitySelectorHeader(
    selectedCity: String,
    savedCities: List<City>,
    isExpanded: Boolean,
    onCityClick: () -> Unit,
    onCitySelected: (City) -> Unit,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCityClick() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column {
                        Text(
                            text = "Current Location",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedCity.ifEmpty { "Select a city" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isExpanded) {
                Divider()

                savedCities.forEach { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCitySelected(city)
                                onCityClick()
                            }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = city.cityName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            fontWeight = if (city.cityName == selectedCity) FontWeight.SemiBold else FontWeight.Normal
                        )

                        if (city.isFavorite) {
                            Icon(
                                Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSearchClick()
                            onCityClick()
                        }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.size(12.dp))
                    Text(
                        "Add new city",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailsContent(
    weather: com.weatherapp.domain.model.Weather,
    temperatureUnit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "Atmospheric Conditions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(Modifier.weight(1f)) {
                    WeatherDetailItem(Icons.Default.WaterDrop,"Humidity","${weather.current.humidity}","%")
                    WeatherDetailItem(Icons.Default.Air,"Wind",
                        if (temperatureUnit == "C") "${weather.current.windKph.toInt()}" else "${weather.current.windMph.toInt()}",
                        if (temperatureUnit == "C") "km/h" else "mph")
                    WeatherDetailItem(Icons.Default.Visibility,"Visibility","${weather.current.visibilityKm.toInt()}","km")
                }

                Column(Modifier.weight(1f)) {
                    WeatherDetailItem(Icons.Default.Compress,"Pressure","${weather.current.pressureMb.toInt()}","mb")
                    WeatherDetailItem(Icons.Default.WbSunny,"UV Index","${weather.current.uv.toInt()}")
                    WeatherDetailItem(Icons.Default.Grain,"Precipitation","${weather.current.precipMm}","mm")
                }
            }

            weather.current.airQuality?.let { air ->
                Spacer(Modifier.height(28.dp))
                Divider()
                Spacer(Modifier.height(18.dp))

                Text(
                    "Air Quality",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ModernAirIndicator("PM2.5", air.pm2_5.toInt(), 50)
                    ModernAirIndicator("PM10", air.pm10.toInt(), 100)
                    ModernAirIndicator("O₃", air.o3.toInt(), 100)
                }
            }
        }
    }
}

@Composable
fun AirQualityIndicator(
    label: String,
    value: Int,
    max: Int
) {
    val percentage = (value.toFloat() / max).coerceIn(0f, 1f)
    val color = when {
        percentage < 0.5 -> MaterialTheme.colorScheme.primary
        percentage < 0.75 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ModernAirIndicator(label: String, value: Int, max: Int) {
    val progress = (value.toFloat() / max).coerceIn(0f, 1f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium)

        Spacer(Modifier.height(8.dp))

        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 6.dp,
            modifier = Modifier.size(56.dp)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    if (isRefreshing) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onSearchClick = {},
            onSettingsClick = {},
            onCitySelected = {}
        )
    }
}