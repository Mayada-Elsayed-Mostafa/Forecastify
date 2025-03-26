package com.example.forecastify.data.remote

import com.example.forecastify.data.models.ForecastResponse
import com.example.forecastify.data.models.WeatherResponse

interface RemoteDataSource {

    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse?

    suspend fun getForecastOfDay(lat: Double, lon: Double): ForecastResponse?
}