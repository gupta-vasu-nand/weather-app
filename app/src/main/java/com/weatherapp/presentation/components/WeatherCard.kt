package com.weatherapp.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    modifier: Modifier = Modifier
) {
    val gradient = getWeatherGradient(
        weather.current.condition.code,
        weather.current.isDay
    )

    val animatedAlpha by rememberInfiniteTransition(label = "overlay")
        .animateFloat(
            initialValue = 0.08f,
            targetValue = 0.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alphaAnim"
        )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradient))
        ) {

            // Soft glass overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = animatedAlpha))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 26.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Top section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = weather.location.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${weather.location.region}, ${weather.location.country}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Text(
                        text = "Updated ${weather.current.lastUpdated.takeLast(5)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                // Center section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = if (temperatureUnit == "C")
                                "${weather.current.tempC.toInt()}°"
                            else
                                "${weather.current.tempF.toInt()}°",
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Feels like ${
                                if (temperatureUnit == "C")
                                    "${weather.current.feelslikeC.toInt()}°C"
                                else
                                    "${weather.current.feelslikeF.toInt()}°F"
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(weather.current.condition.icon)
                                .crossfade(true)
                                .build(),
                            contentDescription = weather.current.condition.text,
                            modifier = Modifier
                                .size(80.dp)
                                .scale(1.2f),
                            contentScale = ContentScale.Fit,
                            error = painterResource(id = R.drawable.ic_weather_default)
                        )

                        Text(
                            text = weather.current.condition.text,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WeatherCardPreview() {
    WeatherCard(
        weather = Weather(
            location = com.weatherapp.domain.model.Location(
                name = "Delhi",
                region = "Ontario",
                country = "Canada",
                lat = 42.85,
                lon = -80.5,
                localtime = "10:59"
            ),
            current = com.weatherapp.domain.model.CurrentWeather(
                lastUpdated = "10:45",
                tempC = 9.6,
                tempF = 49.3,
                isDay = true,
                condition = WeatherCondition("Sunny", "", 1000),
                windMph = 16.6,
                windKph = 26.6,
                windDegree = 213,
                windDir = "SW",
                pressureMb = 1012.0,
                pressureIn = 29.89,
                precipMm = 0.0,
                precipIn = 0.0,
                humidity = 54,
                cloud = 0,
                feelslikeC = 6.3,
                feelslikeF = 43.3,
                visibilityKm = 10.0,
                visibilityMiles = 6.0,
                uv = 1.2,
                gustMph = 30.0,
                gustKph = 48.3,
                airQuality = null
            )
        ),
        temperatureUnit = "C"
    )
}