package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class SaveCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: City): Long {
        return repository.saveCity(city)
    }
}