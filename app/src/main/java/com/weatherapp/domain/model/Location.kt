package com.weatherapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val localtime: String
)