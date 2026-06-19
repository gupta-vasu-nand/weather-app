package com.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("location")
    val location: LocationDto,
    @SerialName("current")
    val current: CurrentWeatherDto
)

@Serializable
data class LocationDto(
    @SerialName("name")
    val name: String,
    @SerialName("region")
    val region: String,
    @SerialName("country")
    val country: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double,
    @SerialName("tz_id")
    val tz_id: String? = null,
    @SerialName("localtime_epoch")
    val localtime_epoch: Long? = null,
    @SerialName("localtime")
    val localtime: String? = null
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("last_updated_epoch")
    val last_updated_epoch: Long,
    @SerialName("last_updated")
    val last_updated: String,
    @SerialName("temp_c")
    val temp_c: Double,
    @SerialName("temp_f")
    val temp_f: Double,
    @SerialName("is_day")
    val is_day: Int,
    @SerialName("condition")
    val condition: ConditionDto,
    @SerialName("wind_mph")
    val wind_mph: Double,
    @SerialName("wind_kph")
    val wind_kph: Double,
    @SerialName("wind_degree")
    val wind_degree: Int,
    @SerialName("wind_dir")
    val wind_dir: String,
    @SerialName("pressure_mb")
    val pressure_mb: Double,
    @SerialName("pressure_in")
    val pressure_in: Double,
    @SerialName("precip_mm")
    val precip_mm: Double,
    @SerialName("precip_in")
    val precip_in: Double,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("cloud")
    val cloud: Int,
    @SerialName("feelslike_c")
    val feelslike_c: Double,
    @SerialName("feelslike_f")
    val feelslike_f: Double,
    @SerialName("windchill_c")
    val windchill_c: Double? = null,
    @SerialName("windchill_f")
    val windchill_f: Double? = null,
    @SerialName("heatindex_c")
    val heatindex_c: Double? = null,
    @SerialName("heatindex_f")
    val heatindex_f: Double? = null,
    @SerialName("dewpoint_c")
    val dewpoint_c: Double? = null,
    @SerialName("dewpoint_f")
    val dewpoint_f: Double? = null,
    @SerialName("vis_km")
    val vis_km: Double,
    @SerialName("vis_miles")
    val vis_miles: Double,
    @SerialName("uv")
    val uv: Double,
    @SerialName("gust_mph")
    val gust_mph: Double,
    @SerialName("gust_kph")
    val gust_kph: Double,
    @SerialName("air_quality")
    val air_quality: AirQualityDto? = null
)

@Serializable
data class ConditionDto(
    @SerialName("text")
    val text: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("code")
    val code: Int
)

@Serializable
data class AirQualityDto(
    @SerialName("co")
    val co: Double,
    @SerialName("no2")
    val no2: Double,
    @SerialName("o3")
    val o3: Double,
    @SerialName("so2")
    val so2: Double,
    @SerialName("pm2_5")
    val pm2_5: Double,
    @SerialName("pm10")
    val pm10: Double,
    @SerialName("us-epa-index")
    val usEpaIndex: Int,
    @SerialName("gb-defra-index")
    val gbDefraIndex: Int
)
