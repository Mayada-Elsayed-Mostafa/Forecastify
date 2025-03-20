package com.example.forecastify.remote

import com.example.forecastify.models.WeatherResponse

interface RemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse?
}