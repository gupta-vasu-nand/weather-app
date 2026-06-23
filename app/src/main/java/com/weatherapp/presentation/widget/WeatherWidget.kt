package com.weatherapp.presentation.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil.Coil
import coil.request.ImageRequest
import com.weatherapp.MainActivity
import com.weatherapp.R
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.utils.getWeatherGradient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

class WeatherWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WeatherWidgetEntryPoint {
        fun repository(): WeatherRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherWidgetEntryPoint::class.java
        )
        val repository = entryPoint.repository()
        val weather = repository.getLastWeather().first()
        val prefs = repository.getUserPreferences().first()

        val iconBitmap = weather?.current?.condition?.icon?.let { iconUrl ->
            downloadIcon(context, iconUrl)
        }

        provideContent {
            val size = LocalSize.current
            GlanceTheme {
                WeatherWidgetContent(
                    weather = weather,
                    tempUnit = if (prefs.temperatureUnit == com.weatherapp.domain.model.TemperatureUnit.CELSIUS) "C" else "F",
                    iconBitmap = iconBitmap,
                    size = size
                )
            }
        }
    }

    private suspend fun downloadIcon(context: Context, iconUrl: String): Bitmap? {
        return try {
            val fullUrl = if (iconUrl.startsWith("//")) "https:$iconUrl" else iconUrl
            val loader = Coil.imageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(fullUrl)
                .size(160, 160)
                .build()
            val result = loader.execute(request)
            val drawable = result.drawable
            if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    private fun WeatherWidgetContent(
        weather: Weather?,
        tempUnit: String,
        iconBitmap: Bitmap? = null,
        size: DpSize
    ) {
        val gradient = getWeatherGradient(
            weather?.current?.condition?.code ?: 1000,
            weather?.current?.isDay ?: true
        )

        val primaryColor = gradient.getOrElse(1) { gradient.first() }
        val accentColor = gradient.getOrElse(0) { gradient.first() }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(primaryColor)
                .cornerRadius(
                    when {
                        size.width < 120.dp -> 20.dp
                        size.width < 220.dp -> 24.dp
                        else -> 30.dp
                    }
                )
                .clickable(actionStartActivity<MainActivity>())
        ) {
            // Enhanced glass overlay
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(
                        if (weather?.current?.isDay == true) {
                            Color.White.copy(alpha = 0.05f)
                        } else {
                            Color.White.copy(alpha = 0.03f)
                        }
                    )
            ) {}

            // Subtle top accent glow
            if (size.width > 200.dp) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(accentColor.copy(alpha = 0.3f))
                        .padding(horizontal = 20.dp)
                ) {}
            }

            if (weather == null) {
                EmptyStateContent()
            } else {
                ResponsiveLayoutSelector(weather, tempUnit, iconBitmap, size)
            }
        }
    }

    @Composable
    private fun EmptyStateContent() {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                        .cornerRadius(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_weather_cloudy),
                        contentDescription = null,
                        modifier = GlanceModifier.size(28.dp)
                    )
                }
                Spacer(GlanceModifier.height(8.dp))
                Text(
                    text = "Weather Unavailable",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Tap to refresh",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }

    @Composable
    private fun ResponsiveLayoutSelector(
        weather: Weather,
        tempUnit: String,
        iconBitmap: Bitmap?,
        size: DpSize
    ) {
        when {
            size.width < 110.dp || size.height < 70.dp -> TinyLayout(weather, tempUnit, iconBitmap, size)
            size.width < 160.dp -> {
                if (size.height < 110.dp) {
                    CompactLayout(weather, tempUnit, iconBitmap, size)
                } else {
                    MediumLayout(weather, tempUnit, iconBitmap, size)
                }
            }
            else -> {
                when {
                    size.height < 110.dp -> CompactLayout(weather, tempUnit, iconBitmap, size)
                    size.height < 200.dp -> MediumLayout(weather, tempUnit, iconBitmap, size)
                    else -> UltraLayout(weather, tempUnit, iconBitmap, size)
                }
            }
        }
    }

    @Composable
    private fun TinyLayout(weather: Weather, tempUnit: String, iconBitmap: Bitmap?, size: DpSize) {
        val temp = if (tempUnit == "C") weather.current.tempC.toInt() else weather.current.tempF.toInt()
        val isVerySmallHeight = size.height < 56.dp
        val isVerySmallWidth = size.width < 90.dp
        
        val iconSize = when {
            isVerySmallHeight -> 22.dp
            isVerySmallWidth -> 28.dp
            else -> 32.dp
        }
        
        val fontSize = when {
            isVerySmallHeight -> 16.sp
            isVerySmallWidth -> 20.sp
            else -> 24.sp
        }

        if (size.height < 60.dp) {
            // Horizontal Layout for wide/short widgets
            Row(
                modifier = GlanceModifier.fillMaxSize().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = if (iconBitmap != null) ImageProvider(iconBitmap) else ImageProvider(R.drawable.ic_weather_default),
                    contentDescription = null,
                    modifier = GlanceModifier.size(iconSize)
                )
                Spacer(GlanceModifier.width(4.dp))
                Text(
                    text = "$temp°",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } else {
            // Vertical Layout for narrow/tall widgets
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(iconSize + 6.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                        .cornerRadius(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = if (iconBitmap != null) ImageProvider(iconBitmap) else ImageProvider(R.drawable.ic_weather_default),
                        contentDescription = null,
                        modifier = GlanceModifier.size(iconSize)
                    )
                }
                Spacer(GlanceModifier.height(2.dp))
                Text(
                    text = "$temp°",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    @Composable
    private fun CompactLayout(weather: Weather, tempUnit: String, iconBitmap: Bitmap?, size: DpSize) {
        val showClock = size.width >= 160.dp
        val showLocation = size.width >= 100.dp
        
        val horizontalPadding = when {
            size.width < 120.dp -> 8.dp
            size.width < 160.dp -> 12.dp
            else -> 16.dp
        }
        
        val iconBoxSize = when {
            size.width < 120.dp -> 32.dp
            size.width < 160.dp -> 40.dp
            else -> 48.dp
        }
        
        val iconSize = when {
            size.width < 120.dp -> 24.dp
            size.width < 160.dp -> 32.dp
            else -> 38.dp
        }

        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with soft glass effect
            Box(
                modifier = GlanceModifier
                    .size(iconBoxSize)
                    .background(Color.White.copy(alpha = 0.1f))
                    .cornerRadius(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = if (iconBitmap != null) ImageProvider(iconBitmap) else ImageProvider(R.drawable.ic_weather_default),
                    contentDescription = null,
                    modifier = GlanceModifier.size(iconSize)
                )
            }
            
            Spacer(GlanceModifier.width(if (size.width < 140.dp) 8.dp else 12.dp))
            
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showLocation) {
                    Text(
                        text = weather.location.name,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = if (size.width < 140.dp) 12.sp else 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (tempUnit == "C") "${weather.current.tempC.toInt()}°" else "${weather.current.tempF.toInt()}°",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = if (size.width < 140.dp) 16.sp else 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    
                    if (size.width >= 130.dp) {
                        Spacer(GlanceModifier.width(6.dp))
                        Text(
                            text = weather.current.condition.text,
                            style = TextStyle(
                                color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            if (showClock) {
                Spacer(GlanceModifier.width(8.dp))
                Box(
                    modifier = GlanceModifier
                        .background(Color.White.copy(alpha = 0.1f))
                        .cornerRadius(16.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    AndroidRemoteViews(remoteViews = RemoteViews("com.weatherapp", R.layout.widget_clock))
                }
            }
        }
    }

    @Composable
    private fun MediumLayout(weather: Weather, tempUnit: String, iconBitmap: Bitmap?, size: DpSize) {
        val showClock = size.width >= 200.dp
        val showMetrics = size.height >= 140.dp
        val showFeelsLike = size.height >= 120.dp
        
        val numMetrics = when {
            size.width < 220.dp -> 2
            size.width < 280.dp -> 3
            else -> 4
        }

        val padding = if (size.width < 180.dp) 12.dp else 16.dp
        val tempTextSize = when {
            size.width < 160.dp -> 32.sp
            size.width < 220.dp -> 44.sp
            else -> 56.sp
        }
        val iconBoxSize = when {
            size.width < 160.dp -> 44.dp
            size.width < 220.dp -> 56.dp
            else -> 64.dp
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(padding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Top Row: Location, Condition & Optional Clock
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = weather.location.name,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = if (size.width < 160.dp) 14.sp else 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = weather.current.condition.text,
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.8f)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
                
                if (showClock) {
                    Box(
                        modifier = GlanceModifier
                            .background(Color.White.copy(alpha = 0.1f))
                            .cornerRadius(12.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        AndroidRemoteViews(remoteViews = RemoteViews("com.weatherapp", R.layout.widget_clock))
                    }
                }
            }

            Spacer(GlanceModifier.defaultWeight())

            // Main Weather Info
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (tempUnit == "C") "${weather.current.tempC.toInt()}°" else "${weather.current.tempF.toInt()}°",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = tempTextSize,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    if (showFeelsLike) {
                        Text(
                            text = "Feels like ${if (tempUnit == "C") weather.current.feelslikeC.toInt() else weather.current.feelslikeF.toInt()}°",
                            style = TextStyle(
                                color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                Spacer(GlanceModifier.defaultWeight())
                
                Box(
                    modifier = GlanceModifier
                        .size(iconBoxSize)
                        .background(Color.White.copy(alpha = 0.1f))
                        .cornerRadius(if (size.width < 200.dp) 16.dp else 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = if (iconBitmap != null) ImageProvider(iconBitmap) else ImageProvider(R.drawable.ic_weather_default),
                        contentDescription = null,
                        modifier = GlanceModifier.size(iconBoxSize * 0.8f)
                    )
                }
            }

            if (showMetrics) {
                Spacer(GlanceModifier.defaultWeight())
                
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    MetricCard(
                        value = "${weather.current.humidity}%",
                        label = "Humidity",
                        iconRes = R.drawable.ic_water_drop,
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Spacer(GlanceModifier.width(6.dp))

                    MetricCard(
                        value = "${weather.current.windKph.toInt()} km/h",
                        label = "Wind",
                        iconRes = R.drawable.ic_wind,
                        modifier = GlanceModifier.defaultWeight()
                    )

                    if (numMetrics >= 3) {
                        Spacer(GlanceModifier.width(6.dp))
                        MetricCard(
                            value = weather.current.uv.toInt().toString(),
                            label = "UV",
                            iconRes = R.drawable.ic_weather_default,
                            modifier = GlanceModifier.defaultWeight()
                        )
                    }

                    if (numMetrics >= 4) {
                        Spacer(GlanceModifier.width(6.dp))
                        MetricCard(
                            value = "${weather.current.pressureMb.toInt()}",
                            label = "hPa",
                            iconRes = R.drawable.ic_pressure,
                            modifier = GlanceModifier.defaultWeight()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun UltraLayout(weather: Weather, tempUnit: String, iconBitmap: Bitmap?, size: DpSize) {
        val showClock = size.width >= 220.dp
        val numMetrics = when {
            size.width < 220.dp -> 2
            size.width < 280.dp -> 3
            else -> 4
        }
        val locationTextSize = when {
            size.width < 220.dp -> 18.sp
            size.width < 280.dp -> 20.sp
            else -> 24.sp
        }
        val tempTextSize = when {
            size.width < 220.dp -> 56.sp
            size.width < 280.dp -> 64.sp
            else -> 88.sp
        }
        val iconBoxSize = when {
            size.width < 220.dp -> 80.dp
            size.width < 280.dp -> 90.dp
            else -> 100.dp
        }
        val iconSize = when {
            size.width < 220.dp -> 64.dp
            size.width < 280.dp -> 72.dp
            else -> 84.dp
        }
        val cornerRadiusVal = when {
            size.width < 220.dp -> 40.dp
            size.width < 280.dp -> 45.dp
            else -> 50.dp
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(if (size.width < 250.dp) 16.dp else 20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Top Row: Location & Clock
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = weather.location.name,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = locationTextSize,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = weather.current.condition.text,
                            style = TextStyle(
                                color = ColorProvider(Color.White.copy(alpha = 0.85f)),
                                fontSize = if (size.width < 220.dp) 12.sp else 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                        Spacer(GlanceModifier.width(8.dp))
                        Box(
                            modifier = GlanceModifier
                                .size(6.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                                .cornerRadius(3.dp)
                        ) {}
                        Spacer(GlanceModifier.width(8.dp))
                        Text(
                            text = weather.current.lastUpdated.substringAfter(" ").take(5),
                            style = TextStyle(
                                color = ColorProvider(Color.White.copy(alpha = 0.5f)),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                if (showClock) {
                    Box(
                        modifier = GlanceModifier
                            .background(Color.White.copy(alpha = 0.1f))
                            .cornerRadius(50.dp)
                            .padding(8.dp)
                    ) {
                        AndroidRemoteViews(
                            remoteViews = RemoteViews(
                                "com.weatherapp",
                                R.layout.widget_clock_large
                            )
                        )
                    }
                }
            }

            Spacer(GlanceModifier.height(if (size.height < 240.dp) 8.dp else 16.dp))

            // Main Weather Display
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = if (tempUnit == "C") "${weather.current.tempC.toInt()}°" else "${weather.current.tempF.toInt()}°",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = tempTextSize,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Feels like ${if (tempUnit == "C") weather.current.feelslikeC.toInt() else weather.current.feelslikeF.toInt()}°",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                            fontSize = if (size.width < 220.dp) 12.sp else 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Box(
                    modifier = GlanceModifier
                        .size(iconBoxSize)
                        .background(Color.White.copy(alpha = 0.08f))
                        .cornerRadius(cornerRadiusVal),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = if (iconBitmap != null) ImageProvider(iconBitmap) else ImageProvider(R.drawable.ic_weather_default),
                        contentDescription = null,
                        modifier = GlanceModifier.size(iconSize)
                    )
                }
            }

            Spacer(GlanceModifier.defaultWeight())

            // Metrics Row - Professional Glass Cards with Icons
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.06f))
                    .cornerRadius(20.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UltraMetricCard(
                    value = "${weather.current.humidity}%",
                    label = "Humidity",
                    iconRes = R.drawable.ic_water_drop,
                    modifier = GlanceModifier.defaultWeight()
                )

                Spacer(GlanceModifier.width(4.dp))

                UltraMetricCard(
                    value = "${weather.current.windKph.toInt()} km/h",
                    label = "Wind",
                    iconRes = R.drawable.ic_wind,
                    modifier = GlanceModifier.defaultWeight()
                )

                if (numMetrics >= 3) {
                    Spacer(GlanceModifier.width(4.dp))
                    UltraMetricCard(
                        value = "${weather.current.pressureMb.toInt()} hPa",
                        label = "Pressure",
                        iconRes = R.drawable.ic_pressure,
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                if (numMetrics >= 4) {
                    Spacer(GlanceModifier.width(4.dp))
                    UltraMetricCard(
                        value = if (tempUnit == "C") "${weather.current.feelslikeC.toInt()}°" else "${weather.current.feelslikeF.toInt()}°",
                        label = "Feels Like",
                        iconRes = R.drawable.ic_thermometer,
                        modifier = GlanceModifier.defaultWeight()
                    )
                }
            }
        }
    }

    @Composable
    private fun MetricCard(
        value: String,
        label: String,
        iconRes: Int,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Column(
            modifier = modifier
                .background(ImageProvider(R.drawable.widget_metric_chip))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = null,
                modifier = GlanceModifier.size(16.dp)
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = value,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                    fontSize = 9.sp
                )
            )
        }
    }

    @Composable
    private fun UltraMetricCard(
        value: String,
        label: String,
        iconRes: Int,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = null,
                modifier = GlanceModifier.size(20.dp)
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = value,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                    fontSize = 10.sp
                )
            )
        }
    }
}