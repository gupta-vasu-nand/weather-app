package com.weatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.weatherapp.R
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.model.WeatherCondition
import com.weatherapp.utils.getWeatherGradient

@Composable
fun WeatherCard(
    weather: Weather,
    temperatureUnit: String,
    modifier: Modifier = Modifier,
    lastUpdateTime: String = ""
) {
    val gradient = getWeatherGradient(
        weather.current.condition.code,
        weather.current.isDay
    )

    // Single directional gradient for premium feel
    val backgroundBrush = Brush.linearGradient(
        colors = gradient,
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp), // Increased height to accommodate all text
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            // Single subtle glow layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            radius = 1000f,
                            center = Offset(200f, 100f)
                        )
                    )
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp) // Slightly reduced padding for more space
            ) {
                // Top Section: Location & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = weather.location.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 22.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${weather.location.region}, ${weather.location.country}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (lastUpdateTime.isNotEmpty()) {
                        Text(
                            text = lastUpdateTime,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Center Section: Temperature & Weather
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Temperature Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (temperatureUnit == "C")
                                "${weather.current.tempC.toInt()}°"
                            else
                                "${weather.current.tempF.toInt()}°",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 76.sp,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.08f),
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                )
                            )
                        )

                        Text(
                            text = "Feels like ${
                                if (temperatureUnit == "C")
                                    "${weather.current.feelslikeC.toInt()}°"
                                else
                                    "${weather.current.feelslikeF.toInt()}°"
                            }",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        )
                    }

                    // Weather Icon and Condition
                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(weather.current.condition.icon)
                                .crossfade(true)
                                .build(),
                            contentDescription = weather.current.condition.text,
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Fit,
                            error = painterResource(id = R.drawable.ic_weather_default)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                Color.White.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = weather.current.condition.text,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    lineHeight = 13.sp
                                ),
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Section: Metrics Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 0.5.dp,
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricItem(
                            icon = Icons.Default.WaterDrop,
                            value = "${weather.current.humidity}%",
                            label = "Humidity"
                        )

                        MetricDivider()

                        MetricItem(
                            icon = Icons.Default.Air,
                            value = if (temperatureUnit == "C")
                                "${weather.current.windKph.toInt()}"
                            else
                                "${weather.current.windMph.toInt()}",
                            label = if (temperatureUnit == "C") "km/h" else "mph"
                        )

                        MetricDivider()

                        MetricItem(
                            icon = Icons.Default.WbSunny,
                            value = weather.current.uv.toInt().toString(),
                            label = "UV Index"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 15.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 9.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetricDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(Color.White.copy(alpha = 0.12f))
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    MaterialTheme {
        WeatherCard(
            weather = Weather(
                location = com.weatherapp.domain.model.Location(
                    name = "Gurgaon",
                    region = "Haryana",
                    country = "India",
                    lat = 28.46,
                    lon = 77.03,
                    localtime = "15:45"
                ),
                current = com.weatherapp.domain.model.CurrentWeather(
                    lastUpdated = "15:30",
                    tempC = 38.1,
                    tempF = 100.6,
                    isDay = true,
                    condition = WeatherCondition(
                        text = "Patchy light rain in area with thunder",
                        icon = "https://cdn.weatherapi.com/weather/128x128/day/302.png",
                        code = 1024
                    ),
                    windMph = 8.9,
                    windKph = 14.4,
                    windDegree = 303,
                    windDir = "WNW",
                    pressureMb = 1003.0,
                    pressureIn = 29.62,
                    precipMm = 0.0,
                    precipIn = 0.0,
                    humidity = 35,
                    cloud = 25,
                    feelslikeC = 37.2,
                    feelslikeF = 99.1,
                    visibilityKm = 6.0,
                    visibilityMiles = 3.0,
                    uv = 3.4,
                    gustMph = 10.3,
                    gustKph = 16.6,
                    airQuality = null
                )
            ),
            temperatureUnit = "C",
            lastUpdateTime = "Updated now"
        )
    }
}