package com.weatherapp.domain.usecase

import com.weatherapp.domain.model.City
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.utils.Resource
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(query: String): Resource<List<City>> {
        if (query.isBlank()) return Resource.Success(emptyList())
        return repository.searchCities(query)
    }
}
