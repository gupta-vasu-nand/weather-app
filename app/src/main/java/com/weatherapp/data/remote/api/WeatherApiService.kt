package com.weatherapp.data.remote.api

import com.weatherapp.data.remote.dto.LocationDto
import com.weatherapp.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
        @Query("aqi") aqi: String = "yes"
    ): WeatherResponseDto

    @GET("current.json")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("q") query: String, // Format: "lat,lon"
        @Query("aqi") aqi: String = "yes"
    ): WeatherResponseDto

    @GET("current.json")
    suspend fun getCurrentWeatherWithAirQuality(
        @Query("q") query: String,
        @Query("aqi") aqi: String = "yes"
    ): WeatherResponseDto

    @GET("search.json")
    suspend fun searchCities(
        @Query("q") query: String
    ): List<LocationDto>

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") query: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "yes"
    ): WeatherResponseDto
}