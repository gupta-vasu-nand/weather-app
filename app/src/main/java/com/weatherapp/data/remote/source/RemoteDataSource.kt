package com.weatherapp.data.remote.source

import com.weatherapp.data.remote.api.WeatherApiService
import com.weatherapp.data.remote.dto.LocationDto
import com.weatherapp.data.remote.dto.WeatherResponseDto
import com.weatherapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: WeatherApiService
) {

    suspend fun getCurrentWeather(city: String): Resource<WeatherResponseDto> {
        return try {
            val response = apiService.getCurrentWeather(city)
            Resource.Success(response)
        } catch (e: HttpException) {
            Resource.Error(
                message = "HTTP Error: ${e.code()}",
                code = e.code()
            )
        } catch (e: IOException) {
            Resource.Error(
                message = "Network Error: ${e.message}",
                isNetworkError = true
            )
        } catch (e: Exception) {
            Resource.Error(
                message = "Unknown Error: ${e.message}"
            )
        }
    }

    suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<WeatherResponseDto> {
        return try {
            val response = apiService.getCurrentWeatherByCoordinates("$lat,$lon")
            Resource.Success(response)
        } catch (e: HttpException) {
            Resource.Error(
                message = "HTTP Error: ${e.code()}",
                code = e.code()
            )
        } catch (e: IOException) {
            Resource.Error(
                message = "Network Error: ${e.message}",
                isNetworkError = true
            )
        } catch (e: Exception) {
            Resource.Error(
                message = "Unknown Error: ${e.message}"
            )
        }
    }

    fun getCurrentWeatherFlow(city: String): Flow<Resource<WeatherResponseDto>> = flow {
        emit(Resource.Loading)
        emit(getCurrentWeather(city))
    }

    suspend fun searchCities(query: String): Resource<List<LocationDto>> {
        return try {
            val response = apiService.searchCities(query)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(
                message = "Search Error: ${e.message}"
            )
        }
    }
}