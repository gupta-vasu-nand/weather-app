package com.weatherapp.presentation.screens.home

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HotTub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.domain.model.City
import com.weatherapp.domain.model.Weather
import com.weatherapp.presentation.components.ErrorView
import com.weatherapp.presentation.components.LoadingOverlay
import com.weatherapp.presentation.components.LoadingView
import com.weatherapp.presentation.components.WeatherCard
import com.weatherapp.presentation.theme.DarkTealPrimary
import com.weatherapp.presentation.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()

    val lastUpdateTimeFormatted = remember(state.lastUpdated) {
        if (state.lastUpdated > 0) {
            DateFormat.format("hh:mm a", state.lastUpdated).toString()
        } else ""
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading && state.weather == null -> {
                LoadingView()
            }

            state.error != null && state.weather == null -> {
                ErrorView(
                    message = state.error ?: "Unknown error",
                    onRetry = { viewModel.retry() }
                )
            }
            else -> {
                LoadingOverlay(isLoading = state.isLoading && !state.isRefreshing) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PullToRefreshBox(
                            state = pullRefreshState,
                            isRefreshing = state.isRefreshing,
                            onRefresh = { viewModel.refreshWeather() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                item(key = "city_selector") {
                                    CitySelectorHeader(
                                        selectedCity = state.selectedCity,
                                        savedCities = state.savedCities,
                                        isExpanded = state.showCitySelector,
                                        onCityClick = { viewModel.toggleCitySelector() },
                                        onCitySelected = { viewModel.selectCity(it) },
                                        onSearchClick = onSearchClick
                                    )
                                }

                                state.weather?.let { weather ->
                                    item(key = "weather_card") {
                                        WeatherCard(
                                            weather = weather,
                                            temperatureUnit = state.temperatureUnit,
                                            modifier = Modifier.animateItem(),
                                            lastUpdateTime = lastUpdateTimeFormatted
                                        )
                                    }

                                    item(key = "network_status") {
                                        NetworkStatusRow(
                                            isOnline = state.isOnline,
                                            lastUpdated = lastUpdateTimeFormatted
                                        )
                                    }

                                    item(key = "weather_details") {
                                        WeatherDetailsContent(
                                            weather = weather,
                                            temperatureUnit = state.temperatureUnit
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkStatusRow(
    isOnline: Boolean,
    lastUpdated: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = if (isOnline) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.SignalWifiOff,
                    contentDescription = null,
                    tint = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isOnline) "Connected" else "Offline Mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (lastUpdated.isNotEmpty()) {
            Text(
                text = "Last updated $lastUpdated",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(TealPrimary, DarkTealPrimary)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCityClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedCity.ifEmpty { "Select a city" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    savedCities.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCitySelected(city)
                                    onCityClick()
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city.cityName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                fontWeight = if (city.cityName == selectedCity) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (city.cityName == selectedCity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )

                            if (city.cityName == selectedCity) {
                                Icon(
                                    Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = {
                            onSearchClick()
                            onCityClick()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .background(buttonGradient)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Manage Cities",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetailsContent(
    weather: Weather,
    temperatureUnit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Atmospheric Conditions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // Use a 2-column grid approach with proper spacing to prevent overflow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailCard(Icons.Default.WaterDrop, "Humidity", "${weather.current.humidity}%")
                    DetailCard(
                        Icons.Default.Air,
                        "Wind Speed",
                        if (temperatureUnit == "C") "${weather.current.windKph} km/h" else "${weather.current.windMph} mph"
                    )
                    DetailCard(
                        Icons.Default.Visibility,
                        "Visibility",
                        "${weather.current.visibilityKm} km"
                    )

                    val dewpoint =
                        if (temperatureUnit == "C") weather.current.dewpointC else weather.current.dewpointF
                    if (dewpoint != null) {
                        DetailCard(
                            Icons.Default.Opacity,
                            "Dew Point",
                            "${dewpoint.toInt()}°$temperatureUnit"
                        )
                    }
                }

                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailCard(Icons.Default.Explore, "Wind Dir", weather.current.windDir)
                    DetailCard(
                        Icons.Default.Compress,
                        "Pressure",
                        "${weather.current.pressureMb} mb"
                    )
                    DetailCard(Icons.Default.WbSunny, "UV Index", "${weather.current.uv}")

                    val heatIndex =
                        if (temperatureUnit == "C") weather.current.heatindexC else weather.current.heatindexF
                    if (heatIndex != null) {
                        DetailCard(
                            Icons.Default.HotTub,
                            "Heat Index",
                            "${heatIndex.toInt()}°$temperatureUnit"
                        )
                    } else {
                        DetailCard(Icons.Default.Grain, "Rain", "${weather.current.precipMm} mm")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            DetailCard(
                Icons.Default.DeviceThermostat,
                "Feels Like",
                "${if (temperatureUnit == "C") weather.current.feelslikeC.toInt() else weather.current.feelslikeF.toInt()}°$temperatureUnit"
            )


            weather.current.airQuality?.let { air ->
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Air Quality",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    val aqiStatus = when (air.usEpaIndex) {
                        1 -> "Good"
                        2 -> "Moderate"
                        3 -> "Unhealthy (Sensitive)"
                        4 -> "Unhealthy"
                        5 -> "Very Unhealthy"
                        else -> "Hazardous"
                    }

                    Surface(
                        color = when (air.usEpaIndex) {
                            1 -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            2 -> Color(0xFFFFC107).copy(alpha = 0.1f)
                            else -> Color(0xFFF44336).copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = aqiStatus,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = when (air.usEpaIndex) {
                                1 -> Color(0xFF4CAF50)
                                2 -> Color(0xFFFFA000)
                                else -> Color(0xFFD32F2F)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AirQualityCard("PM2.5", air.pm2_5.toInt(), 50, "µg/m³")
                    AirQualityCard("PM10", air.pm10.toInt(), 100, "µg/m³")
                    AirQualityCard("O₃", air.o3.toInt(), 100, "µg/m³")
                    AirQualityCard("CO", air.co.toInt(), 1000, "µg/m³")
                    AirQualityCard("NO₂", air.no2.toInt(), 200, "µg/m³")
                }
            }
        }
    }
}

@Composable
fun DetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AirQualityCard(label: String, value: Int, max: Int, unit: String) {
    val progress = (value.toFloat() / max).coerceIn(0f, 1f)
    val color = when {
        progress < 0.3 -> Color(0xFF4CAF50)
        progress < 0.6 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(44.dp),
                    color = color,
                    strokeWidth = 4.dp,
                    trackColor = color.copy(alpha = 0.1f)
                )
                Text(
                    value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onSearchClick = {}
        )
    }
}