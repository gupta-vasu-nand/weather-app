package com.weatherapp.data.remote.dto

import com.weatherapp.domain.model.AirQuality
import com.weatherapp.domain.model.CurrentWeather
import com.weatherapp.domain.model.Location
import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.model.WeatherCondition

fun WeatherResponseDto.toDomain(): Weather {
    return Weather(
        location = location.toDomain(),
        current = current.toDomain()
    )
}

fun LocationDto.toDomain(): Location {
    return Location(
        name = name,
        region = region,
        country = country,
        lat = lat,
        lon = lon,
        localtime = localtime ?: ""
    )
}

fun CurrentWeatherDto.toDomain(): CurrentWeather {
    return CurrentWeather(
        lastUpdated = last_updated,
        tempC = temp_c,
        tempF = temp_f,
        isDay = is_day == 1,
        condition = condition.toDomain(),
        windMph = wind_mph,
        windKph = wind_kph,
        windDegree = wind_degree,
        windDir = wind_dir,
        pressureMb = pressure_mb,
        pressureIn = pressure_in,
        precipMm = precip_mm,
        precipIn = precip_in,
        humidity = humidity,
        cloud = cloud,
        feelslikeC = feelslike_c,
        feelslikeF = feelslike_f,
        heatindexC = heatindex_c,
        heatindexF = heatindex_f,
        dewpointC = dewpoint_c,
        dewpointF = dewpoint_f,
        visibilityKm = vis_km,
        visibilityMiles = vis_miles,
        uv = uv,
        gustMph = gust_mph,
        gustKph = gust_kph,
        airQuality = air_quality?.toDomain()
    )
}

fun ConditionDto.toDomain(): WeatherCondition {
    return WeatherCondition(
        text = text,
        icon = "https:$icon", // Add protocol
        code = code
    )
}

fun AirQualityDto.toDomain(): AirQuality {
    return AirQuality(
        co = co,
        no2 = no2,
        o3 = o3,
        so2 = so2,
        pm2_5 = pm2_5,
        pm10 = pm10,
        usEpaIndex = usEpaIndex,
        gbDefraIndex = gbDefraIndex
    )
}

fun LocationDto.toCity(): com.weatherapp.domain.model.City {
    return com.weatherapp.domain.model.City(
        cityName = name,
        lat = lat,
        lon = lon,
        type = com.weatherapp.domain.model.CityType.OTHER
    )
}
