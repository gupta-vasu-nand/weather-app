package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class DeleteCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: City) {
        repository.deleteCity(city)
    }
}