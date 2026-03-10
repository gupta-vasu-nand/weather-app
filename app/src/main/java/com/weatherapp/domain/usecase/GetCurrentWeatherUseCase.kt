package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.Weather
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): Resource<Weather> {
        return repository.getCurrentWeather(city)
    }

    suspend operator fun invoke(lat: Double, lon: Double): Resource<Weather> {
        return repository.getCurrentWeather(lat, lon)
    }

    fun getCachedWeather(): Flow<Weather?> {
        return repository.getLastWeather()
    }
}