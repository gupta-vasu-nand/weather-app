package com.weatherapp.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(
        val message: String,
        val code: Int? = null,
        val isNetworkError: Boolean = false
    ) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}